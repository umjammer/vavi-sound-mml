/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ConvFMML.Common.Key;
import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.MMLStyle;
import ConvFMML.Data.Intermediate.Event.Instrument;
import ConvFMML.Data.Intermediate.Event.KeySignature;
import ConvFMML.Data.Intermediate.Event.Note;
import ConvFMML.Data.Intermediate.Event.NoteRest;
import ConvFMML.Data.Intermediate.Event.Pan;
import ConvFMML.Data.Intermediate.Event.Rest;
import ConvFMML.Data.Intermediate.Event.Tempo;
import ConvFMML.Data.Intermediate.Event.TimeSignature;
import ConvFMML.Data.Intermediate.Event.Volume;
import ConvFMML.Data.Intermediate.Intermediate;
import ConvFMML.Data.Intermediate.Notes;
import ConvFMML.Data.Intermediate.NotesStatus;
import ConvFMML.Data.Intermediate.Position;
import ConvFMML.Data.Intermediate.Track;
import ConvFMML.Data.MML.Bar;
import ConvFMML.Data.MML.Command.Command;
import ConvFMML.Data.MML.Command.Length;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;
import ConvFMML.Settings;
import ConvFMML.Tables;


public abstract class IntermediateToMMLConverter {

    public static IntermediateToMMLConverter Factory(MMLStyle mmlStyle) {
        IntermediateToMMLConverter instance;

        switch (mmlStyle) {
            case Custom:
                instance = new IntermediateToCustomMMLConverter();
                break;
            case FMP:
                instance = new IntermediateToFMPMMLConverter();
                break;
            case FMP7:
                instance = new IntermediateToFMP7MMLConverter();
                break;
            case MXDRV:
                instance = new IntermediateToMXDRVMMLConverter();
                break;
            case NRTDRV:
                instance = new IntermediateToNRTDRVMMLConverter();
                break;
            case PMD:
                instance = new IntermediateToPMDMMLConverter();
                break;
            case MUCOM88:
                instance = new IntermediateToMUCOM88MMLConverter();
                break;
            case Mml2vgm:
                instance = new IntermediateToMml2vgmMMLConverter();
                break;
            default:
                instance = null;
                break;
        }

        return instance;
    }

    public MML Convert(Intermediate intermediate, Settings settings, List<NotesStatus> statusList) {
        try {
            List<Part> partlist = CreatePartList(intermediate, settings, statusList);

            return CreateMMLInstance(partlist, intermediate.getTitle(), intermediate.getCountsPerWholeNote());
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorConverterFailedToMML, ex);
        }
    }

    private List<Part> CreatePartList(Intermediate intermediate, Settings settings, List<NotesStatus> statusList) {
        int[][] lentable = CreateLengthTable((int) settings.getMmlExpression().getTimeBase().intValue(), settings.getNoteRest());


        var partList = new ArrayList<Part>();

        Iterator<Tempo> tempoNode = intermediate.getTempoList().iterator();
        for (Track track : intermediate.getTrackList()) {
            Notes notes = track.getNotesList().get(0);
            NotesStatus status = statusList.stream().filter(x -> (x.getTrackNumber() == track.getNumber() && x.getNumberInTrack() == notes.getNumberInTrack()));

            Iterator<NoteRest> noteNode = notes.getNoteList().iterator();
            Iterator<Instrument> instNode = track.getInstrumentList().iterator();
            Iterator<Volume> volNode = track.getVolumeList().iterator();
            Iterator<Pan> panNode = track.getPanList().iterator();
            Iterator<KeySignature> ksNode = intermediate.getKeySignatureList().iterator();
            Iterator<TimeSignature> tsNode = intermediate.getTimeSignatureList().iterator();

            int barCnt = 1;
            Key key = Key.CMaj;
            var barList = new ArrayList<Bar>();
            boolean firstNote = true;

            for (int i = 1; i <= track.getLength().getBar(); i++) {
                var comList = new LinkedList<Command>();
                boolean prevTied = false;

                int /* MMLCommandRelation */ relation = MMLCommandRelation.Clear.ordinal();
                while (true) {
                    String addCommandName;
                    if (intermediate.getTrackList().indexOf(track) == 0) {
                        addCommandName = GetEarliestCommandName(ksNode.Value, tempoNode.Value, instNode.Value, volNode.Value, panNode.Value, noteNode.Value, i, prevTied, settings);

                    } else {
                        addCommandName = GetEarliestCommandName(ksNode.Value, null, instNode.Value, volNode.Value, panNode.Value, noteNode.Value, i, prevTied, settings);
                    }

                    Command addCommand = null;
                    if (addCommandName == typeof(KeySignature).Name) {
                        key = ksNode.Value.Key;
                        ksNode = ksNode.Next;
                        continue;
                    } else if (addCommandName == typeof(Tempo).Name) {
                        if (comList.size() > 0) {
                            comList.Last.Value.CommandRelation |= MMLCommandRelation.NextControl;
                        }

                        addCommand = CreateTempoInstance(tempoNode.Value.Value, relation);

                        relation |= MMLCommandRelation.PrevControl;
                        tempoNode = tempoNode.Next;
                    } else if (addCommandName == typeof(Instrument).Name) {
                        if (comList.size() > 0) {
                            comList.getLast().Value.CommandRelation |= MMLCommandRelation.NextControl;
                        }

                        addCommand = CreateInstrumentInstance(instNode.Value.Value, relation);

                        relation |= MMLCommandRelation.PrevControl;
                        instNode = instNode.Next;
                    } else if (addCommandName == typeof(Volume).Name) {
                        if (!comList.isEmpty()) {
                            comList.getLast().Value.CommandRelation |= MMLCommandRelation.NextControl;
                        }

                        addCommand = CreateVolumeInstance(volNode.Value.Value, relation);

                        relation |= MMLCommandRelation.PrevControl;
                        volNode = volNode.Next;
                    } else if (addCommandName == typeof(Pan).Name) {
                        if (!comList.isEmpty()) {
                            comList.Last.Value.CommandRelation |= MMLCommandRelation.NextControl;
                        }

                        addCommand = CreatePanInstance(panNode.Value.Value, relation);

                        relation |= MMLCommandRelation.PrevControl;
                        panNode = panNode.Next;
                    } else if (addCommandName == typeof(Note).Name) {
                        if (!comList.isEmpty()) {
                            comList.Last.Value.CommandRelation &= ~MMLCommandRelation.NextControl;
                        }

                        var note = (Note) noteNode.Value;
                        Position poslen = CalculateLength(note.Start, note.End, intermediate.TimeSignatureList, (int) settings.getMmlExpression().TimeBase);
                        List<Integer> length = ConvertLengthFormat(poslen, lentable);
                        MMLCommandRelation tempRel = relation;
                        if (note.TieFlag) {
                            tempRel |= MMLCommandRelation.TieAfter;
                            relation |= MMLCommandRelation.TieBefore;
                        } else {
                            tempRel &= ~MMLCommandRelation.TieAfter;
                            relation &= ~MMLCommandRelation.TieBefore;
                        }

                        addCommand = CreateNoteInstance((note.KeyNumber / 12 - 1), Tables.NoteNameDictionary.get(key)[note.KeyNumber % 12], length, tempRel);

                        relation &= ~MMLCommandRelation.PrevControl;
                        prevTied = note.isTieFlag();
                        noteNode = noteNode.Next;
                    } else if (addCommandName == typeof(Rest).Name) {
                        if (firstNote) {
                            addCommand = CreateLengthInstance(8, relation);
                            comList.addLast(addCommand);

                            firstNote = false;
                        }


                        if (!comList.isEmpty()) {
                            comList.Last.Value.CommandRelation &= ~MMLCommandRelation.NextControl;
                        }

                        var rest = (Rest) noteNode.Value;
                        Position poslen = CalculateLength(rest.getStart(), rest.getEnd(), intermediate.getTimeSignatureList(), (int) settings.mmlExpression.TimeBase);
                        List<Integer> length = ConvertLengthFormat(poslen, lentable);

                        MMLCommandRelation tempRel = relation;
                        if (rest.isTieFlag()) {
                            tempRel |= MMLCommandRelation.TieAfter;
                            relation |= MMLCommandRelation.TieBefore;
                        } else {
                            tempRel &= ~MMLCommandRelation.TieAfter;
                            relation &= ~MMLCommandRelation.TieBefore;
                        }

                        addCommand = CreateRestInstance(length, tempRel);

                        relation &= ~MMLCommandRelation.PrevControl;
                        prevTied = rest.TieFlag;
                        noteNode = noteNode.Next;
                    } else {
                        break;
                    }

                    comList.addLast(addCommand);
                }


                if (settings.getMmlExpression().getNewBlockByBar() == 1 && i == tsNode.Value.Position.Bar) {
                    barCnt = 1;
                } else {
                    barCnt++;
                }

                int nextTSBar = (tsNode.Next.Value.Position.Bar ? ? 0);
                if (i + 1 == nextTSBar) {
                    tsNode = tsNode.Next;
                }
                String seperateSign = GetSeperateSign(i, barCnt, nextTSBar, settings.getMmlExpression());


                barList.add(new Bar(comList, i, seperateSign));
            }

            if (track.getLength().getTick() == 0) {
                Bar lastBar = barList.getLast();
                if (lastBar.getCommandList().isEmpty()) {
                    barList.remove(lastBar);
                }
            }

            partList.add(new Part(barList, status.getSoundModule(), track.getName()));
        }

        return partList;
    }

    private int[][] CreateLengthTable(int countsPerWholeNote, Settings.NoteRest settings) {
        // Create length elements
        var elements = new ArrayList<LengthElement>();
        int div = 1;
        int divtri;
        while (true) {
            if (countsPerWholeNote % div == 0) {
                elements.add(new LengthElement(div, ((int) countsPerWholeNote / div), false));
                divtri = div * 3;
                if (countsPerWholeNote % divtri == 0) {
                    elements.add(new LengthElement(divtri, ((int) countsPerWholeNote / divtri), true));
                }
                div <<= 1;
            } else {
                break;
            }
        }
        elements = elements.sort(x -> x.getLength()).toList();

        // Create length table
        var table = new LengthContainer[countsPerWholeNote];
        table[0] = new LengthContainer();
        table[0].AddLengthElement(elements.stream().filter(x -> x.getLength() == 1));
        elements.remove(elements.stream().filter(x -> x.getLength() == 1));     // Remove whole note

        for (int i = 0; i < elements.size(); i++) {
            FillLengthTableLoop(table, elements, i, new LengthContainer(), settings);
        }

        return Arrays.stream(table).map(x -> x.GetLength(settings)).toArray();
    }

    private void FillLengthTableLoop(LengthContainer[] table, List<LengthElement> elements, int n, LengthContainer container, Settings.NoteRest settings) {
        if (elements.size() == n) return;

        LengthContainer newContainer = container.clone();
        int loopcnt = (elements.get(n).isTripletFlag() ? 2 : 1);

        for (int i = 0; i < loopcnt; i++) {
            newContainer.AddLengthElement(elements.get(n));
            if (newContainer.getGate() >= table.length) return;

            if (table[newContainer.getGate()] == null) {
                table[newContainer.getGate()] = newContainer;
            } else {
                if (settings.getLengthStyle() == 0) {
                    if (newContainer.getCount() < table[newContainer.getGate()].getCount()) {
                        table[newContainer.getGate()] = newContainer;
                    } else if (newContainer.getCount() == table[newContainer.getGate()].getCount()) {
                        if (newContainer.getTripletCount() < table[newContainer.getGate()].getTripletCount()) {
                            table[newContainer.getGate()] = newContainer;
                        }
                    }
                } else {
                    if (newContainer.getTripletCount() < table[newContainer.getGate()].getTripletCount()) {
                        table[newContainer.getGate()] = newContainer;
                    }
                }
            }

            for (int j = n + 1; j < elements.size(); j++) {
                FillLengthTableLoop(table, elements, j, newContainer, settings);
            }

            if (elements.get(n).isTripletFlag()) {
                newContainer = container.clone();
            }
        }
    }

    private String GetEarliestCommandName(
            KeySignature ks,
            Tempo tempo,
            Instrument inst,
            Volume volume,
            Pan pan,
            NoteRest nr,
            int barNumber,
            boolean prevTied,
            Settings settings
    ) {
        Position pos;
        String name;

        if (ks == null) {
            pos = null;
            name = "";
        } else {
            if (barNumber < ks.getPosition().getBar()) {
                if ((settings.getNoteRest().getTieStyle() == 1 ||
                        (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                        prevTied) {
                    pos = ks.getPosition();
                    name = ks.getClass().getName();
                } else {
                    pos = null;
                    name = "";
                }
            } else {
                pos = ks.getPosition();
                name = ks.getClass().getName();
            }
        }

        if (tempo != null) {
            if (barNumber == tempo.getPosition().getBar() ||
                    (settings.getNoteRest().getTieStyle() == 1 ||
                            (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                            prevTied) {
                if (pos == null || pos.compareTo(tempo.getPosition()) > 0) {
                    pos = tempo.getPosition();
                    name = tempo.getClass().getName();
                }
            }
        }

        if (inst != null) {
            if (barNumber == inst.getPosition().getBar() ||
                    (settings.getNoteRest().getTieStyle() == 1 ||
                            (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                            prevTied) {
                if (pos == null || pos.compareTo(inst.getPosition()) > 0) {
                    pos = inst.getPosition();
                    name = inst.getClass().getName();
                }
            }
        }

        if (volume != null) {
            if (barNumber == volume.getPosition().getBar() ||
                    (settings.getNoteRest().getTieStyle() == 1 ||
                            (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                            prevTied) {
                if (pos == null || pos.compareTo(volume.getPosition()) > 0) {
                    pos = volume.getPosition();
                    name = volume.getClass().getName();
                }
            }
        }

        if (pan != null) {
            if (barNumber == pan.getPosition().getBar() ||
                    (settings.getNoteRest().getTieStyle() == 1 ||
                            (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                            prevTied) {
                if (pos == null || pos.compareTo(pan.getPosition()) > 0) {
                    pos = pan.getPosition();
                    name = pan.getClass().getName();
                }
            }
        }

        if (nr != null) {
            if (barNumber == nr.getStart().getBar() ||
                    (settings.getNoteRest().getTieStyle() == 1 ||
                            (settings.getNoteRest().getTieStyle() == 0 && settings.getNoteRest().getCutByBar() == 1 && !settings.getNoteRest().isNewBlockInCutted())) &&
                            prevTied) {
                if (pos == null || pos.compareTo(nr.getStart()) > 0) {
                    name = nr.getClass().getName();
                }
            }
        }

        return name;
    }

    private Position CalculateLength(
            Position start,
            Position end,
            LinkedList<TimeSignature> tsList,
            int countsPerWholeNote
    ) {
        // Search current time signature
        Iterator<TimeSignature> curTSNode = tsList.iterator();
        while (true) {
            if (curTSNode.Value.Position.compareTo(start) < 0) {
                if (curTSNode.Next == null) {
                    break;
                } else {
                    curTSNode = curTSNode.Next;
                }
            } else if (curTSNode.Value.Position.compareTo(start) == 0) {
                break;
            } else {
                curTSNode = curTSNode.Previous;
                break;
            }
        }

        // Calculate length data by Position
        Position len = new Position(0, 0);
        Position tempStart = start;
        Iterator<TimeSignature> nextTSNode = curTSNode.Next;

        while (true) {
            if (nextTSNode == null) {
                Position sub = end.Subtract(tempStart, curTSNode.Value.TickPerBar);
                Position sub2 = Position.ConvertByTicksPerBar(sub, curTSNode.Value.TickPerBar, (int) countsPerWholeNote);
                len = len.add(sub2, (int) countsPerWholeNote);
                break;
            } else {
                if (nextTSNode.Value.PrevSignedPosition.compareTo(end) >= 0) {
                    Position sub = end.Subtract(tempStart, curTSNode.Value.TickPerBar);
                    Position sub2 = Data.Intermediate.Position.ConvertByTicksPerBar(sub, curTSNode.Value.TickPerBar, (int) countsPerWholeNote);
                    len = len.add(sub2, (int) countsPerWholeNote);
                    break;
                } else {
                    Position sub = nextTSNode.Value.PrevSignedPosition.Subtract(tempStart, curTSNode.Value.TickPerBar);
                    Position sub2 = Data.Intermediate.Position.ConvertByTicksPerBar(sub, curTSNode.Value.TickPerBar, (int) countsPerWholeNote);
                    len = len.add(sub2, (int) countsPerWholeNote);
                    tempStart = nextTSNode.Value.Position;
                    curTSNode = curTSNode.Next;
                    nextTSNode = nextTSNode.Next;
                }
            }
        }

        return len;
    }

    private List<Integer> ConvertLengthFormat(Position posdata, int[][] lentable) {
        var lenList = new ArrayList<Integer>();
        int bar = posdata.getBar();
        int tick = posdata.getTick();

        for (; bar > 0; bar--) {
            lenList.addAll(lentable[0]);
        }

        if (tick > 0) {
            lenList.addAll(lentable[tick]);
        }

        return lenList;
    }

    protected abstract ConvFMML.Data.MML.Command.Tempo CreateTempoInstance(int value, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Volume CreateVolumeInstance(int value, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Pan CreatePanInstance(int value, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation);

    protected abstract ConvFMML.Data.MML.Command.Length CreateLengthInstance(int value, MMLCommandRelation relation);

    private String GetSeperateSign(int curBar, int barCnt, int tsBar, Settings.MMLExpression settings) {
        if (settings.getNewLineByTimeSignature() == 1 && curBar + 1 == tsBar) {
            return System.lineSeparator();
        }

        if (settings.getNewBlockByBar() != 0) {
            if (settings.getNewLineBarCount().intValue() != 0 && barCnt % settings.getNewLineBarCount().intValue() == 0) {
                return System.lineSeparator();
            } else {
                switch (settings.getNewBlockByBar()) {
                    case 1:
                        return " ";
                    case 2:
                        return "\t";
                    default:
                        return "";
                }
            }
        } else {
            return "";
        }
    }

    protected abstract MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholenote);
}
