/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Modifier;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ConvFMML.Common.MMLStyle;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.Intermediate.Event.Instrument;
import ConvFMML.Data.Intermediate.Event.Note;
import ConvFMML.Data.Intermediate.Event.NoteRest;
import ConvFMML.Data.Intermediate.Event.Pan;
import ConvFMML.Data.Intermediate.Event.Rest;
import ConvFMML.Data.Intermediate.Event.Tempo;
import ConvFMML.Data.Intermediate.Event.Volume;
import ConvFMML.Data.Intermediate.Intermediate;
import ConvFMML.Data.Intermediate.Notes;
import ConvFMML.Data.Intermediate.NotesStatus;
import ConvFMML.Data.Intermediate.Position;
import ConvFMML.Data.Intermediate.Track;
import ConvFMML.Settings;


public abstract class MusicDataModifier {

    private class CommandPartSet {

        private LinkedList<ChangeEventSet> instrumentSetList;
        private LinkedList<ChangeEventSet> volumeSetList;
        private LinkedList<ChangeEventSet> panSetList;

        public LinkedList<ChangeEventSet> getInstrumentSetList() {
            return instrumentSetList;
        }

        public void setInstrumentSetList(LinkedList<ChangeEventSet> instrumentSetList) {
            this.instrumentSetList = instrumentSetList;
        }

        public LinkedList<ChangeEventSet> getVolumeSetList() {
            return volumeSetList;
        }

        public void setVolumeSetList(LinkedList<ChangeEventSet> volumeSetList) {
            this.volumeSetList = volumeSetList;
        }

        public LinkedList<ChangeEventSet> getPanSetList() {
            return panSetList;
        }

        public void setPanSetList(LinkedList<ChangeEventSet> panSetList) {
            this.panSetList = panSetList;
        }
    }

    public static MusicDataModifier Factory(MMLStyle mmlStyle) {
        MusicDataModifier instance;

        switch (mmlStyle) {
            case Custom:
                instance = new CustomMusicDataModifier();
                break;
            case FMP:
                instance = new FMPMusicDataModifier();
                break;
            case FMP7:
                instance = new FMP7MusicDataModifier();
                break;
            case MXDRV:
                instance = new MXDRVMusicDataModifier();
                break;
            case NRTDRV:
                instance = new NRTDRVMusicDataModifier();
                break;
            case PMD:
                instance = new PMDMusicDataModifier();
                break;
            case MUCOM88:
                instance = new MUCOM88MusicDataModifier();
                break;
            case Mml2vgm:
                instance = new Mml2vgmMusicDataModifier();
                break;
            default:
                instance = null;
                break;
        }

        return instance;
    }

    public Intermediate Modify(Intermediate src, Settings settings, List<NotesStatus> statusList) {
        Intermediate mod = ConstructPartFromTrack(src, settings, statusList);

        mod = InsertRests(mod);

        LinkedList<TempoSet> tempoList = CreateTempoSetList(mod.TempoList);
        List<CommandPartSet> commandPartList = CreateCommandPartSetList(mod.TrackList);


        if (settings.controlCommand.generic.Invalid == 1) {
            DeleteInvalidControlCommands(mod, commandPartList);
        }

        if (settings.controlCommand.generic.SamePosition == 1) {
            ModifySamePositionControlCommands(mod, tempoList, commandPartList);
        }

        if (settings.controlCommand.generic.Predeclared == 1) {
            DeletePredeclaredControlCommands(mod, tempoList, commandPartList, settings, statusList);
        }

        mod = ConvertTempoSetList(mod, tempoList);
        mod = ConvertCommandPartSetList(mod, commandPartList);


        mod = CutNoteByControlCommands(mod);

        if (settings.noteRest.CutByBar != 0) {
            mod = CutNoteByBar(mod);
        }


        return mod;
    }

    private Intermediate ConstructPartFromTrack(Intermediate src, Settings settings, List<NotesStatus> statusList) {
        try {
            Settings.ControlCommand comSettings = settings.controlCommand;
            var newTrackList = new ArrayList<>();

            for (NotesStatus ns : statusList) {
                if (ns.Printable) {
                    Track track = src.TrackList.Find(x -> x.Number == ns.TrackNumber);

                    var newNotesList = new ArrayList<Notes>();
                    if (track.Polyphony > 0) {
                        Notes notes = track.NotesList.Find(x -> x.NumberInTrack == ns.NumberInTrack);
                        newNotesList.add(notes);
                    }

                    var newInstList = new LinkedList<Instrument>();
                    if (comSettings.programChange.Enable) {
                        for (Instrument inst : track.InstrumentList) {
                            newInstList.addLast(inst.clone());
                        }
                    }

                    var newVolumeList = new LinkedList<Volume>();
                    if (comSettings.volume.Enable) {
                        for (Volume vol : track.VolumeList) {
                            newVolumeList.addLast(vol.clone());
                        }
                    }

                    var newPanList = new LinkedList<Pan>();
                    newPanList = ClonePanList(newPanList, track.PanList, settings, ns.SoundModule);


                    newTrackList.add(new Track(newNotesList, newInstList, newVolumeList, newPanList, track.Number, track.Length, ns.Name));
                }
            }

            LinkedList<Tempo> newTempoList;
            if (comSettings.tempo.Enable) {
                newTempoList = src.TempoList;
            } else {
                newTempoList = new LinkedList<Tempo>();
            }

            return new Intermediate(newTrackList, src.TimeSignatureList, src.KeySignatureList, newTempoList, src.Title, src.size()sPerWholeNote);
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierPartReconstruct, ex);
        }
    }

    protected abstract LinkedList<Pan> ClonePanList(LinkedList<Pan> newList, LinkedList<Pan> srcList, Settings settings, SoundModule module) {
        if (settings.controlCommand.pan.Enable && module == SoundModule.FM) {
            for (Pan pan : srcList) {
                newList.addLast(pan.clone());
            }
        }
        return newList;
    }

    private Intermediate InsertRests(Intermediate src) {
        var query = src.TrackList.Select(x -> x.Length);
        Position newLength = query.Max();

        var newTrackList = new ArrayList<Track>();
        for (Track track : src.TrackList) {
            Notes notes = track.NotesList[0];
            var newNoteList = new LinkedList<NoteRest>();

            if (notes.NoteList.size() > 0) {
                Iterator<NoteRest> noteNode = notes.NoteList.iterator();
                var prevEnd = new Position(1, 0);

                for (; noteNode != null; noteNode = noteNode.Next) {
                    if (noteNode.Value.Start.compareTo(prevEnd) > 0) {
                        newNoteList.addLast(new Rest(prevEnd.clone(), noteNode.Value.Start.clone()));
                    }
                    newNoteList.addLast(noteNode.Value);
                    prevEnd = noteNode.Value.End;
                }

                if (newNoteList.Last.Value.End.compareTo(newLength) < 0) {
                    newNoteList.addLast(new Rest(newNoteList.Last.Value.End.clone(), newLength.clone()));
                }
            } else {
                newNoteList.addLast(new Rest(new Position(1, 0), newLength.clone()));
            }

            var newNotesList = new ArrayList<Notes>();
            newNotesList.add(new Notes(newNoteList, notes.NumberInTrack));
            newTrackList.add(new Track(newNotesList, track.InstrumentList, track.VolumeList, track.PanList, track.Number, newLength, track.Name));
        }

        return new Intermediate(newTrackList, src.TimeSignatureList, src.KeySignatureList, src.TempoList, src.Title, src.size()sPerWholeNote);
    }

    private LinkedList<TempoSet> CreateTempoSetList(LinkedList<Tempo> tempoList) {
        var list = new LinkedList<TempoSet>();
        for (Tempo t : tempoList) {
            list.addLast(CreateTempoSet(t));
        }
        return list;
    }

    private List<CommandPartSet> CreateCommandPartSetList(List<Track> trackList) {
        var list = new ArrayList<CommandPartSet>();

        for (Track track : trackList) {
            var set = new CommandPartSet();

            var instlist = new LinkedList<ChangeEventSet>();
            for (Instrument i : track.InstrumentList) {
                instlist.addLast(CreateInstrumentSet(i));
            }
            set.InstrumentSetList = instlist;

            var vollist = new LinkedList<ChangeEventSet>();
            for (Volume v : track.VolumeList) {
                vollist.addLast(CreateVolumeSet(v));
            }
            set.VolumeSetList = vollist;

            var panlist = new LinkedList<ChangeEventSet>();
            for (Pan p : track.PanList) {
                panlist.addLast(CreatePanSet(p));
            }
            set.PanSetList = panlist;

            list.add(set);
        }

        return list;
    }

    protected abstract TempoSet CreateTempoSet(Tempo t);

    protected abstract ChangeEventSet CreateInstrumentSet(Instrument i);

    protected abstract ChangeEventSet CreateVolumeSet(Volume v);

    protected abstract ChangeEventSet CreatePanSet(Pan p);

    private void DeleteInvalidControlCommands(Intermediate src, List<CommandPartSet> partSet) {
        try {
            int i = 0;
            for (Track track : src.TrackList) {
                Iterator<NoteRest> noteNode = track.NotesList[0].NoteList.iterator();
                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> instNode = partSet[i].InstrumentSetList.iterator();

                while (noteNode != null && instNode != null) {
                    NoteRest curNote = noteNode.Value;
                    ChangeEventSet curInst = instNode.Value;

                    if (curNote.End.compareTo(curInst.Position) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (instNode.Next != null) {
                            if (instNode.Next.Value.Position.compareTo(curNote.End) > 0) {
                                newInstrumentSetList.addLast(curInst);
                            }
                        } else {
                            if (noteNode.Next != null) {
                                newInstrumentSetList.addLast(curInst);
                            }
                        }
                    } else {
                        newInstrumentSetList.addLast(curInst);
                    }

                    instNode = instNode.Next;
                }
                partSet[i].InstrumentSetList = newInstrumentSetList;


                noteNode = track.NotesList[0].NoteList.iterator();
                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> volNode = partSet[i].VolumeSetList.iterator();

                while (noteNode != null && volNode != null) {
                    NoteRest curNote = noteNode.Value;
                    ChangeEventSet curVol = volNode.Value;

                    if (curNote.End.compareTo(curVol.Position) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (volNode.Next != null) {
                            if (volNode.Next.Value.Position.compareTo(curNote.End) > 0) {
                                newVolumeSetList.addLast(curVol);
                            }
                        } else {
                            if (noteNode.Next != null) {
                                newVolumeSetList.addLast(curVol);
                            }
                        }
                    } else {
                        newVolumeSetList.addLast(curVol);
                    }

                    volNode = volNode.Next;
                }
                partSet.get(i).setVolumeSetList(newVolumeSetList);


                noteNode = track.getNotesList().get(0).getNoteList().iterator();
                var newPansetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> panNode = partSet.get(i).getPanSetList().iterator();

                while (noteNode != null && panNode != null) {
                    NoteRest curNote = noteNode.Value;
                    ChangeEventSet curPan = panNode.Value;

                    if (curNote.getEnd().compareTo(curPan.getPosition()) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (panNode.Next != null) {
                            if (panNode.Next.Value.Position.compareTo(curNote.getEnd()) > 0) {
                                newPansetList.addLast(curPan);
                            }
                        } else {
                            if (noteNode.Next != null) {
                                newPansetList.addLast(curPan);
                            }
                        }
                    } else {
                        newPansetList.addLast(curPan);
                    }

                    panNode = panNode.Next;
                }
                partSet[i].PanSetList = newPansetList;

                i++;
            }
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierRemoveUselessCommands, ex);
        }
    }

    private void ModifySamePositionControlCommands(Intermediate src, LinkedList<TempoSet> tempoSetList, List<CommandPartSet> partSet) {
        try {
            for (CommandPartSet set : partSet) {
                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curInstNode = set.instrumentSetList.iterator();
                while (curInstNode != null) {
                    if (curInstNode.Previous != null) {
                        if (curInstNode.Value.Position.compareTo(curInstNode.Previous.Value.Position) == 0) {
                            newInstrumentSetList.removeLast();
                        }
                    }
                    newInstrumentSetList.addLast(curInstNode.Value);
                    curInstNode = curInstNode.Next;
                }
                set.InstrumentSetList = newInstrumentSetList;

                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curVolNode = set.getVolumeSetList().iterator();
                while (curVolNode != null) {
                    if (curVolNode.Previous != null) {
                        if (curVolNode.Value.Position.compareTo(curVolNode.Previous.Value.Position) == 0) {
                            newVolumeSetList.removeLast();
                        }
                    }
                    newVolumeSetList.addLast(curVolNode.Value);
                    curVolNode = curVolNode.Next;
                }
                set.setVolumeSetList(newVolumeSetList);

                var newPanSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curPanNode = set.getPanSetList().iterator();
                while (curPanNode != null) {
                    if (curPanNode.Previous != null) {
                        if (curPanNode.Value.Position.compareTo(curPanNode.Previous.Value.Position) == 0) {
                            newPanSetList.removeLast();
                        }
                    }
                    newPanSetList.addLast(curPanNode.Value);
                    curPanNode = curPanNode.Next;
                }
                set.panSetList = newPanSetList;
            }

            var newTempoSetList = new LinkedList<TempoSet>();
            Iterator<TempoSet> curTempoNode = tempoSetList.iterator();
            while (curTempoNode != null) {
                if (curTempoNode.Previous != null) {
                    if (curTempoNode.Value.Position.compareTo(curTempoNode.Previous.Value.Position) == 0) {
                        newTempoSetList.removeLast();
                    }
                }
                newTempoSetList.addLast(curTempoNode.Value);
                curTempoNode = curTempoNode.Next;
            }
            tempoSetList = newTempoSetList;
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierOptimizeSamePlaceType, ex);
        }
    }

    private void DeletePredeclaredControlCommands(Intermediate src, LinkedList<TempoSet> tempoSetList, List<CommandPartSet> partSet, Settings settings, List<NotesStatus> statusList) {
        try {
            NotesStatus status = null;
            AtomicInteger i = new AtomicInteger(0);
            for (CommandPartSet set : partSet) {
                NotesStatus sts = statusList.stream().filter(x -> (x.getTrackNumber() == src.getTrackList().get(i.get()).getNumber() && x.NumberInTrack == src.TrackList[i].NotesList[0].NumberInTrack));
                if (i.get() == 0) {
                    status = sts;
                }

                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curInstNode = set.getInstrumentSetList().iterator();
                while (curInstNode != null) {
                    if (curInstNode.Previous == null ||
                            curInstNode.Value.ToString(settings, sts.getSoundModule()) != curInstNode.Previous.Value.ToString(settings, sts.setSoundModule();)) {
                        newInstrumentSetList.addLast(curInstNode.Value);
                    }
                    curInstNode = curInstNode.Next;
                }
                set.setInstrumentSetList(newInstrumentSetList);

                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curVolNode = set.getVolumeSetList().iterator();
                while (curVolNode != null) {
                    if (curVolNode.Previous == null ||
                            curVolNode.Value.ToString(settings, sts.getSoundModule()) != curVolNode.Previous.Value.ToString(settings, sts.getSoundModule())) {
                        newVolumeSetList.addLast(curVolNode.Value);
                    }
                    curVolNode = curVolNode.Next;
                }
                set.setVolumeSetList(newVolumeSetList);

                var newPanSetList = new LinkedList<ChangeEventSet>();
                Iterator<ChangeEventSet> curPanNode = set.PanSetList.iterator();
                while (curPanNode != null) {
                    if (curPanNode.Previous == null ||
                            curPanNode.Value.ToString(settings, sts.getSoundModule()) != curPanNode.Previous.Value.ToString(settings, sts.getSoundModule())) {
                        newPanSetList.addLast(curPanNode.Value);
                    }
                    curPanNode = curPanNode.Next;
                }
                set.setPanSetList(newPanSetList);

                i.getAndIncrement();
            }

            var newTempoSetList = new LinkedList<TempoSet>();
            Iterator<TempoSet> curTempoNode = tempoSetList.iterator();
            while (curTempoNode != null) {
                if (curTempoNode.Previous == null ||
                        curTempoNode.Value.ToString(settings, status.getSoundModule()) != curTempoNode.Previous.Value.ToString(settings, status.getSoundModule())) {
                    newTempoSetList.addLast(curTempoNode.Value);
                }
                curTempoNode = curTempoNode.Next;
            }
            tempoSetList = newTempoSetList;
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierOptimizeSameTypeValue, ex);
        }
    }

    private Intermediate ConvertTempoSetList(Intermediate src, LinkedList<TempoSet> tempoSetList) {
        return new Intermediate(src.getTrackList(), src.getTimeSignatureList(), src.getKeySignatureList(), ConvertTempoSetList(tempoSetList), src.getTitle(), src.getCountsPerWholeNote());
    }

    private Intermediate ConvertCommandPartSetList(Intermediate src, List<CommandPartSet> commandPartList) {
        var newTrackList = new ArrayList<Track>();

        int i = 0;
        for (Track track : src.getTrackList()) {
            CommandPartSet set = commandPartList.get(i);
            newTrackList.add(new Track(track.getNotesList(), ConvertInstrumentSetList(set.instrumentSetList), ConvertVolumeSetList(set.volumeSetList), ConvertPanSetList(set.panSetList), track.getNumber(), track.getLength(), track.getName()));
            i++;
        }

        return new Intermediate(newTrackList, src.getTimeSignatureList(), src.getKeySignatureList(), src.getTempoList(), src.getTitle(), src.getCountsPerWholeNote());
    }

    private LinkedList<Tempo> ConvertTempoSetList(LinkedList<TempoSet> setList) {
        var list = new LinkedList<Tempo>();
        setList.forEach(x -> list.addLast(x.getData()));
        return list;
    }

    private LinkedList<Instrument> ConvertInstrumentSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Instrument>();
        setList.forEach(x -> list.addLast((Instrument) x.getData()));
        return list;
    }

    private LinkedList<Volume> ConvertVolumeSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Volume>();
        setList.forEach(x -> list.addLast((Volume) x.getData()));
        return list;
    }

    private LinkedList<Pan> ConvertPanSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Pan>();
        setList.forEach(x -> list.addLast((Pan) x.getData()));
        return list;
    }

    private Intermediate CutNoteByControlCommands(Intermediate src) {
        try {
            for (Track track : src.getTrackList()) {
                Iterator<NoteRest> noteNode = track.getNotesList().get(0).getNoteList().iterator();
                if (src.getTrackList().indexOf(track) == 0) {
                    Iterator<Tempo> tempoNode = src.getTempoList().iterator();

                    while (tempoNode != null) {
                        NoteRest curNote = noteNode.Value;
                        Tempo curTempo = tempoNode.Value;

                        if (curNote.End.compareTo(curTempo.getPosition()) <= 0) {
                            noteNode = noteNode.Next;
                            continue;
                        }

                        if (curNote.Start.compareTo(curTempo.Position) < 0) {
                            NoteRest newNote;
                            if (curNote instanceof Note) {
                                var nt = (Note) curNote;
                                newNote = new Note(curTempo.Position.clone(), nt.End.clone(), nt.KeyNumber, nt.Velocity);
                            } else {
                                var rst = (Rest) curNote;
                                newNote = new Rest(curTempo.Position.clone(), rst.End.clone());
                            }
                            track.NotesList[0].NoteList.addAfter(noteNode, newNote);

                            curNote.setEnd(curTempo.getPosition().clone());
                            curNote.setTieFlag(true);
                        }

                        tempoNode = tempoNode.Next;
                    }
                }


                noteNode = track.getNotesList().get(0).getNoteList().iterator();
                Iterator<Instrument> instNode = track.getInstrumentList().iterator();

                while (instNode != null) {
                    NoteRest curNote = noteNode.Value;
                    Instrument curInst = instNode.Value;

                    if (curNote.getEnd().compareTo(curInst.getPosition()) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote.getStart().compareTo(curInst.getPosition()) < 0) {
                        NoteRest newNote;
                        if (curNote instanceof Note nt) {
                            newNote = new Note(curInst.getPosition().clone(), nt.getEnd().clone(), nt.getKeyNumber(), nt.getVelocity());
                        } else {
                            var rst = (Rest) curNote;
                            newNote = new Rest(curInst.getPosition().clone(), rst.getEnd().clone());
                        }
                        track.getNotesList().get(0).getNoteList().add(noteNode, newNote);

                        curNote.setEnd(curInst.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    instNode = instNode.Next;
                }


                noteNode = track.getNotesList().get(0).getNoteList().iterator();
                Iterator<Volume> volNode = track.getVolumeList().iterator();

                while (volNode != null) {
                    NoteRest curNote = noteNode.Value;
                    Volume curVol = volNode.Value;

                    if (curNote.getEnd().compareTo(curVol.getPosition()) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote.getStart().compareTo(curVol.getPosition()) < 0) {
                        NoteRest newNote;
                        if (curNote instanceof Note) {
                            var nt = (Note) curNote;
                            newNote = new Note(curVol.getPosition().clone(), nt.getEnd().clone(), nt.getKeyNumber(), nt.getVelocity());
                        } else {
                            var rst = (Rest) curNote;
                            newNote = new Rest(curVol.getPosition().clone(), rst.getEnd().clone());
                        }
                        track.getNotesList().get(0).getNoteList().add(noteNode, newNote);

                        curNote.setEnd(curVol.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    volNode = volNode.Next;
                }


                noteNode = track.getNotesList().get(0).getNoteList().iterator();
                Iterator<Pan> panNode = track.getPanList().iterator();

                while (panNode != null) {
                    NoteRest curNote = noteNode.Value;
                    Pan curPan = panNode.Value;

                    if (curNote.getEnd().compareTo(curPan.getPosition()) <= 0) {
                        noteNode = noteNode.Next;
                        continue;
                    }

                    if (curNote.getStart().compareTo(curPan.getPosition()) < 0) {
                        NoteRest newNote;
                        if (curNote instanceof Note nt) {
                            newNote = new Note(curPan.getPosition().clone(), nt.getEnd().clone(), nt.getKeyNumber(), nt.getVelocity());
                        } else {
                            var rst = (Rest) curNote;
                            newNote = new Rest(curPan.getPosition().clone(), rst.getEnd().clone());
                        }
                        track.getNotesList().get(0).getNoteList().add(noteNode, newNote);

                        curNote.setEnd(curPan.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    panNode = panNode.Next;
                }
            }


            return src;
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierCutByCommands, ex);
        }
    }

    private Intermediate CutNoteByBar(Intermediate src) {
        try {
            for (Track track : src.getTrackList()) {
                Iterator<NoteRest> noteNode = track.getNotesList().get(0).getNoteList().iterator();
                for (; noteNode != null; noteNode = noteNode.Next) {
                    NoteRest nr = noteNode.Value;
                    if (nr.getStart().getBar() < nr.getEnd().getBar() &&
                            (nr.getEnd().getBar() != nr.getStart().getBar() + 1 || nr.getEnd().getTick() != 0)) {
                        NoteRest newNr;
                        var newPos = new Position(nr.getStart().getBar() + 1, 0);

                        if (nr instanceof Note) {
                            var note = (Note) nr;
                            newNr = new Note(newPos.clone(), note.getEnd().clone(), note.getKeyNumber(), note.getVelocity());
                        } else {
                            var rest = (Rest) nr;
                            newNr = new Rest(newPos.clone(), rest.getEnd().clone());
                        }
                        track.getNotesList().get(0).getNoteList().add(noteNode, newNr);

                        nr.setEnd(newPos.clone());
                        nr.setTieFlag(true);
                    }
                }
            }

            return src;
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorModifierCutByBar, ex);
        }
    }
}
