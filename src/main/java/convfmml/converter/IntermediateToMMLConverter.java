/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import convfmml.Common.Key;
import convfmml.Common.MMLCommandRelation;
import convfmml.Common.MMLStyle;
import convfmml.data.intermediate.event.Instrument;
import convfmml.data.intermediate.event.KeySignature;
import convfmml.data.intermediate.event.Note;
import convfmml.data.intermediate.event.NoteRest;
import convfmml.data.intermediate.event.Pan;
import convfmml.data.intermediate.event.Rest;
import convfmml.data.intermediate.event.Tempo;
import convfmml.data.intermediate.event.TimeSignature;
import convfmml.data.intermediate.event.Volume;
import convfmml.data.intermediate.Intermediate;
import convfmml.data.intermediate.Notes;
import convfmml.data.intermediate.NotesStatus;
import convfmml.data.intermediate.Position;
import convfmml.data.intermediate.Track;
import convfmml.data.mml.Bar;
import convfmml.data.mml.command.Command;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;
import convfmml.Settings;
import convfmml.Tables;


public abstract class IntermediateToMMLConverter {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    public static IntermediateToMMLConverter factory(MMLStyle mmlStyle) {
        IntermediateToMMLConverter instance = switch (mmlStyle) {
            case Custom -> new IntermediateToCustomMMLConverter();
            case FMP -> new IntermediateToFMPMMLConverter();
            case FMP7 -> new IntermediateToFMP7MMLConverter();
            case MXDRV -> new IntermediateToMXDRVMMLConverter();
            case NRTDRV -> new IntermediateToNRTDRVMMLConverter();
            case PMD -> new IntermediateToPMDMMLConverter();
            case MUCOM88 -> new IntermediateToMUCOM88MMLConverter();
            case Mml2vgm -> new IntermediateToMml2vgmMMLConverter();
            default -> null;
        };

        return instance;
    }

    public MML convert(Intermediate intermediate, Settings settings, List<NotesStatus> statusList) {
        try {
            List<Part> partlist = createPartList(intermediate, settings, statusList);

            return createMMLInstance(partlist, intermediate.getTitle(), intermediate.getCountsPerWholeNote());
        } catch (Exception ex) {
            throw new IllegalStateException(rb.getString("ErrorConverterFailedToMML"), ex);
        }
    }

    private List<Part> createPartList(Intermediate intermediate, Settings settings, List<NotesStatus> statusList) {
        int[][] lentable = createLengthTable((int) settings.getMmlExpression().getTimeBase().intValue(), settings.getNoteRest());


        var partList = new ArrayList<Part>();

        Iterator<Tempo> tempoNodeI = intermediate.getTempoList().iterator();
        Tempo tempoNode = tempoNodeI.next();
        for (Track track : intermediate.getTrackList()) {
            Notes notes = track.getNotesList().getFirst();
            NotesStatus status = statusList.stream().filter(x -> (x.getTrackNumber() == track.getNumber() && x.getNumberInTrack() == notes.getNumberInTrack())).findFirst().orElse(null);

            Iterator<NoteRest> noteNodeI = notes.getNoteList().iterator();
            NoteRest noteNode = noteNodeI.next();
            Iterator<Instrument> instNodeI = track.getInstrumentList().iterator();
            Instrument instNode = instNodeI.next();
            Iterator<Volume> volNodeI = track.getVolumeList().iterator();
            Volume volNode = volNodeI.next();
            Iterator<Pan> panNodeI = track.getPanList().iterator();
            Pan panNode = panNodeI.next();
            Iterator<KeySignature> ksNodeI = intermediate.getKeySignatureList().iterator();
            KeySignature ksNode = ksNodeI.next();
            Iterator<TimeSignature> tsNodeI = intermediate.getTimeSignatureList().iterator();
            TimeSignature tsNode = tsNodeI.next();

            int barCnt = 1;
            Key key = Key.CMaj;
            var barList = new ArrayList<Bar>();
            boolean firstNote = true;

            for (int i = 1; i <= track.getLength().getBar(); i++) {
                var comList = new LinkedList<Command>();
                boolean prevTied = false;

                EnumSet<MMLCommandRelation> relation = EnumSet.of(MMLCommandRelation.Clear);
                while (true) {
                    String addCommandName;
                    if (intermediate.getTrackList().indexOf(track) == 0) {
                        addCommandName = getEarliestCommandName(ksNode, tempoNode, instNode, volNode, panNode, noteNode, i, prevTied, settings);

                    } else {
                        addCommandName = getEarliestCommandName(ksNode, null, instNode, volNode, panNode, noteNode, i, prevTied, settings);
                    }

                    Command addCommand = null;
                    if (addCommandName.equals(KeySignature.class.getName())) {
                        key = ksNode.getKey();
                        ksNode = ksNodeI.next();
                        continue;
                    } else if (addCommandName.equals(Tempo.class.getName())) {
                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().add(MMLCommandRelation.NextControl);
                        }

                        addCommand = createTempoInstance(tempoNode.getValue(), relation);

                        relation.add(MMLCommandRelation.PrevControl);
                        tempoNode = tempoNodeI.next();
                    } else if (addCommandName.equals(Instrument.class.getName())) {
                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().add(MMLCommandRelation.NextControl);
                        }

                        addCommand = createInstrumentInstance(instNode.getValue(), relation);

                        relation.add(MMLCommandRelation.PrevControl);
                        instNode = instNodeI.next();
                    } else if (addCommandName.equals(Volume.class.getName())) {
                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().add(MMLCommandRelation.NextControl);
                        }

                        addCommand = createVolumeInstance(volNode.getValue(), relation);

                        relation.add(MMLCommandRelation.PrevControl);
                        volNode = volNodeI.next();
                    } else if (addCommandName.equals(Pan.class.getName())) {
                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().add(MMLCommandRelation.NextControl);
                        }

                        addCommand = createPanInstance(panNode.getValue(), relation);

                        relation.add(MMLCommandRelation.PrevControl);
                        panNode = panNodeI.next();
                    } else if (addCommandName.equals(Note.class.getName())) {
                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().remove(MMLCommandRelation.NextControl);
                        }

                        var note = (Note) noteNode;
                        Position poslen = calculateLength(note.getStart(), note.getEnd(), intermediate.getTimeSignatureList(), settings.getMmlExpression().getTimeBase().intValue());
                        List<Integer> length = convertLengthFormat(poslen, lentable);
                        EnumSet<MMLCommandRelation> tempRel = relation.clone();
                        if (note.isTieFlag()) {
                            tempRel.add(MMLCommandRelation.TieAfter);
                            relation.add(MMLCommandRelation.TieBefore);
                        } else {
                            tempRel.remove(MMLCommandRelation.TieAfter);
                            relation.remove(MMLCommandRelation.TieBefore);
                        }

                        addCommand = createNoteInstance((note.getKeyNumber() / 12 - 1), Tables.NoteNameDictionary.get(key)[note.getKeyNumber() % 12], length, tempRel);

                        relation.remove(MMLCommandRelation.PrevControl);
                        prevTied = note.isTieFlag();
                        noteNode = noteNodeI.next();
                    } else if (addCommandName.equals(Rest.class.getName())) {
                        if (firstNote) {
                            addCommand = createLengthInstance(8, relation);
                            comList.addLast(addCommand);

                            firstNote = false;
                        }


                        if (!comList.isEmpty()) {
                            comList.getLast().getCommandRelation().remove(MMLCommandRelation.NextControl);
                        }

                        var rest = (Rest) noteNode;
                        Position poslen = calculateLength(rest.getStart(), rest.getEnd(), intermediate.getTimeSignatureList(), settings.getMmlExpression().getTimeBase().intValue());
                        List<Integer> length = convertLengthFormat(poslen, lentable);

                        EnumSet<MMLCommandRelation> tempRel = relation.clone();
                        if (rest.isTieFlag()) {
                            tempRel.add(MMLCommandRelation.TieAfter);
                            relation.add(MMLCommandRelation.TieBefore);
                        } else {
                            tempRel.remove(MMLCommandRelation.TieAfter);
                            relation.remove(MMLCommandRelation.TieBefore);
                        }

                        addCommand = createRestInstance(length, tempRel);

                        relation.remove(MMLCommandRelation.PrevControl);
                        prevTied = rest.isTieFlag();
                        noteNode = noteNodeI.next();
                    } else {
                        break;
                    }

                    comList.addLast(addCommand);
                }


                if (settings.getMmlExpression().getNewBlockByBar() == 1 && i == tsNode.getPosition().getBar()) {
                    barCnt = 1;
                } else {
                    barCnt++;
                }

                int nextTSBar = (tsNodeI.next() /* .getPosition().getBar() */ != null ? tsNodeI.next().getPosition().getBar() : 0); // TODO vavi
                if (i + 1 == nextTSBar) {
                    tsNode = tsNodeI.next();
                }
                String seperateSign = getSeparateSign(i, barCnt, nextTSBar, settings.getMmlExpression());


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

    private static int[][] createLengthTable(int countsPerWholeNote, Settings.NoteRest settings) {
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
        elements.sort(Comparator.comparingInt(LengthElement::getLength));

        // Create length table
        var table = new LengthContainer[countsPerWholeNote];
        table[0] = new LengthContainer();
        table[0].addLengthElement(elements.stream().filter(x -> x.getLength() == 1).findFirst().orElse(null));
        elements.remove(elements.stream().filter(x -> x.getLength() == 1).findFirst().orElse(null));     // Remove whole note

        for (int i = 0; i < elements.size(); i++) {
            fillLengthTableLoop(table, elements, i, new LengthContainer(), settings);
        }

        return Arrays.stream(table).map(x -> x.getLength(settings)).toArray(int[][]::new);
    }

    private static void fillLengthTableLoop(LengthContainer[] table, List<LengthElement> elements, int n, LengthContainer container, Settings.NoteRest settings) {
        if (elements.size() == n) return;

        LengthContainer newContainer = container.clone();
        int loopcnt = (elements.get(n).isTripletFlag() ? 2 : 1);

        for (int i = 0; i < loopcnt; i++) {
            newContainer.addLengthElement(elements.get(n));
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
                fillLengthTableLoop(table, elements, j, newContainer, settings);
            }

            if (elements.get(n).isTripletFlag()) {
                newContainer = container.clone();
            }
        }
    }

    private static String getEarliestCommandName(
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

    private static Position calculateLength(
            Position start,
            Position end,
            LinkedList<TimeSignature> tsList,
            int countsPerWholeNote
    ) {
        // Search current time signature
        ListIterator<TimeSignature> curTSNodeI = tsList.listIterator();
        TimeSignature curTSNode = curTSNodeI.next();
        while (true) {
            if (curTSNode.getPosition().compareTo(start) < 0) {
                if (!curTSNodeI.hasNext()) {
                    break;
                } else {
                    curTSNode = curTSNodeI.next();
                }
            } else if (curTSNode.getPosition().compareTo(start) == 0) {
                break;
            } else {
                curTSNode = curTSNodeI.previous();
                break;
            }
        }

        // Calculate length data by Position
        Position len = new Position(0, 0);
        Position tempStart = start;
        TimeSignature nextTSNode = curTSNodeI.next();

        while (true) {
            if (nextTSNode == null) {
                Position sub = end.subtract(tempStart, curTSNode.getTickPerBar());
                Position sub2 = Position.convertByTicksPerBar(sub, curTSNode.getTickPerBar(), countsPerWholeNote);
                len = len.add(sub2, countsPerWholeNote);
                break;
            } else {
                if (nextTSNode.getPrevSignedPosition().compareTo(end) >= 0) {
                    Position sub = end.subtract(tempStart, curTSNode.getTickPerBar());
                    Position sub2 = Position.convertByTicksPerBar(sub, curTSNode.getTickPerBar(), countsPerWholeNote);
                    len = len.add(sub2, countsPerWholeNote);
                    break;
                } else {
                    Position sub = nextTSNode.getPrevSignedPosition().subtract(tempStart, curTSNode.getTickPerBar());
                    Position sub2 = Position.convertByTicksPerBar(sub, curTSNode.getTickPerBar(), countsPerWholeNote);
                    len = len.add(sub2, countsPerWholeNote);
                    tempStart = nextTSNode.getPosition();
                    curTSNode = curTSNodeI.next();
                    nextTSNode = curTSNodeI.next();
                }
            }
        }

        return len;
    }

    private static List<Integer> convertLengthFormat(Position posdata, int[][] lentable) {
        var lenList = new ArrayList<Integer>();
        int bar = posdata.getBar();
        int tick = posdata.getTick();

        for (; bar > 0; bar--) {
            lenList.addAll(Arrays.stream(lentable[0]).boxed().toList());
        }

        if (tick > 0) {
            lenList.addAll(Arrays.stream(lentable[tick]).boxed().toList());
        }

        return lenList;
    }

    protected abstract convfmml.data.mml.command.Tempo createTempoInstance(int value, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Instrument createInstrumentInstance(int value, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Volume createVolumeInstance(int value, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Pan createPanInstance(int value, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Note createNoteInstance(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Rest createRestInstance(List<Integer> length, EnumSet<MMLCommandRelation> relation);

    protected abstract convfmml.data.mml.command.Length createLengthInstance(int value, EnumSet<MMLCommandRelation> relation);

    private static String getSeparateSign(int curBar, int barCnt, int tsBar, Settings.MMLExpression settings) {
        if (settings.getNewLineByTimeSignature() == 1 && curBar + 1 == tsBar) {
            return System.lineSeparator();
        }

        if (settings.getNewBlockByBar() != 0) {
            if (settings.getNewLineBarCount().intValue() != 0 && barCnt % settings.getNewLineBarCount().intValue() == 0) {
                return System.lineSeparator();
            } else {
                return switch (settings.getNewBlockByBar()) {
                    case 1 -> " ";
                    case 2 -> "\t";
                    default -> "";
                };
            }
        } else {
            return "";
        }
    }

    protected abstract MML createMMLInstance(List<Part> partList, String title, int countsPerWholeNote);
}
