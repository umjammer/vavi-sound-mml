/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.modifier;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import convfmml.Common.MMLStyle;
import convfmml.Common.SoundModule;
import convfmml.Settings;
import convfmml.data.intermediate.Intermediate;
import convfmml.data.intermediate.Notes;
import convfmml.data.intermediate.NotesStatus;
import convfmml.data.intermediate.Position;
import convfmml.data.intermediate.Track;
import convfmml.data.intermediate.event.Instrument;
import convfmml.data.intermediate.event.Note;
import convfmml.data.intermediate.event.NoteRest;
import convfmml.data.intermediate.event.Pan;
import convfmml.data.intermediate.event.Rest;
import convfmml.data.intermediate.event.Tempo;
import convfmml.data.intermediate.event.Volume;


public abstract class MusicDataModifier {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private static class CommandPartSet {

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

    public static MusicDataModifier factory(MMLStyle mmlStyle) {
        MusicDataModifier instance = switch (mmlStyle) {
            case Custom -> new CustomMusicDataModifier();
            case FMP -> new FMPMusicDataModifier();
            case FMP7 -> new FMP7MusicDataModifier();
            case MXDRV -> new MXDRVMusicDataModifier();
            case NRTDRV -> new NRTDRVMusicDataModifier();
            case PMD -> new PMDMusicDataModifier();
            case MUCOM88 -> new MUCOM88MusicDataModifier();
            case Mml2vgm -> new Mml2vgmMusicDataModifier();
            default -> null;
        };

        return instance;
    }

    public Intermediate modify(Intermediate src, Settings settings, List<NotesStatus> statusList) {
        Intermediate mod = constructPartFromTrack(src, settings, statusList);

        mod = insertRests(mod);

        LinkedList<TempoSet> tempoList = createTempoSetList(mod.getTempoList());
        List<CommandPartSet> commandPartList = createCommandPartSetList(mod.getTrackList());


        if (settings.getControlCommand().getGeneric().getInvalid() == 1) {
            deleteInvalidControlCommands(mod, commandPartList);
        }

        if (settings.getControlCommand().getGeneric().getSamePosition() == 1) {
            modifySamePositionControlCommands(mod, tempoList, commandPartList);
        }

        if (settings.getControlCommand().getGeneric().getPredeclared() == 1) {
            deletePredeclaredControlCommands(mod, tempoList, commandPartList, settings, statusList);
        }

        mod = convertTempoSetList(mod, tempoList);
        mod = convertCommandPartSetList(mod, commandPartList);

        mod = cutNoteByControlCommands(mod);

        if (settings.getNoteRest().getCutByBar() != 0) {
            mod = cutNoteByBar(mod);
        }

        return mod;
    }

    private static Intermediate constructPartFromTrack(Intermediate src, Settings settings, List<NotesStatus> statusList) {
        try {
            Settings.ControlCommand comSettings = settings.getControlCommand();
            var newTrackList = new ArrayList<Track>();

            for (NotesStatus ns : statusList) {
                if (ns.isPrintable()) {
                    Track track = src.getTrackList().stream().filter(x -> x.getNumber() == ns.getTrackNumber()).findFirst().orElse(null);

                    var newNotesList = new ArrayList<Notes>();
                    if (track.getPolyphony() > 0) {
                        Notes notes = track.getNotesList().stream().filter(x -> x.getNumberInTrack() == ns.getNumberInTrack()).findFirst().orElse(null);
                        newNotesList.add(notes);
                    }

                    var newInstList = new LinkedList<Instrument>();
                    if (comSettings.getProgramChange().isEnable()) {
                        for (Instrument inst : track.getInstrumentList()) {
                            newInstList.addLast(inst.clone());
                        }
                    }

                    var newVolumeList = new LinkedList<Volume>();
                    if (comSettings.getVolume().isEnable()) {
                        for (Volume vol : track.getVolumeList()) {
                            newVolumeList.addLast(vol.clone());
                        }
                    }

                    var newPanList = new LinkedList<Pan>();
                    newPanList = clonePanList(newPanList, track.getPanList(), settings, ns.getSoundModule());

                    newTrackList.add(new Track(newNotesList, newInstList, newVolumeList, newPanList, track.getNumber(), track.getLength(), ns.getName()));
                }
            }

            LinkedList<Tempo> newTempoList;
            if (comSettings.getTempo().isEnable()) {
                newTempoList = src.getTempoList();
            } else {
                newTempoList = new LinkedList<Tempo>();
            }

            return new Intermediate(newTrackList, src.getTimeSignatureList(), src.getKeySignatureList(), newTempoList, src.getTitle(), src.getCountsPerWholeNote());
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierPartReconstruct"), ex);
        }
    }

    public static LinkedList<Pan> clonePanList(LinkedList<Pan> newList, LinkedList<Pan> srcList, Settings settings, SoundModule module) {
        if (settings.getControlCommand().getPan().isEnable() && module == SoundModule.FM) {
            for (Pan pan : srcList) {
                newList.addLast(pan.clone());
            }
        }
        return newList;
    }

    private static Intermediate insertRests(Intermediate src) {
        Position newLength = src.getTrackList().stream()
                .map(Track::getLength)
                .max(Comparator.naturalOrder())
                .orElseThrow();

        var newTrackList = new ArrayList<Track>();
        for (Track track : src.getTrackList()) {
            Notes notes = track.getNotesList().getFirst();
            var newNoteList = new LinkedList<NoteRest>();

            if (!notes.getNoteList().isEmpty()) {
                var prevEnd = new Position(1, 0);

                for (NoteRest noteNode : notes.getNoteList()) {
                    if (noteNode.getStart().compareTo(prevEnd) > 0) {
                        newNoteList.addLast(new Rest(prevEnd.clone(), noteNode.getStart().clone()));
                    }
                    newNoteList.addLast(noteNode);
                    prevEnd = noteNode.getEnd();
                }

                if (newNoteList.getLast().getEnd().compareTo(newLength) < 0) {
                    newNoteList.addLast(new Rest(newNoteList.getLast().getEnd().clone(), newLength.clone()));
                }
            } else {
                newNoteList.addLast(new Rest(new Position(1, 0), newLength.clone()));
            }

            var newNotesList = new ArrayList<Notes>();
            newNotesList.add(new Notes(newNoteList, notes.getNumberInTrack()));
            newTrackList.add(new Track(newNotesList, track.getInstrumentList(), track.getVolumeList(), track.getPanList(), track.getNumber(), newLength, track.getName()));
        }

        return new Intermediate(newTrackList, src.getTimeSignatureList(), src.getKeySignatureList(), src.getTempoList(), src.getTitle(), src.getCountsPerWholeNote());
    }

    private LinkedList<TempoSet> createTempoSetList(LinkedList<Tempo> tempoList) {
        var list = new LinkedList<TempoSet>();
        for (Tempo t : tempoList) {
            list.addLast(createTempoSet(t));
        }
        return list;
    }

    private List<CommandPartSet> createCommandPartSetList(List<Track> trackList) {
        var list = new ArrayList<CommandPartSet>();

        for (Track track : trackList) {
            var set = new CommandPartSet();

            var instlist = new LinkedList<ChangeEventSet>();
            for (Instrument i : track.getInstrumentList()) {
                instlist.addLast(createInstrumentSet(i));
            }
            set.setInstrumentSetList(instlist);

            var vollist = new LinkedList<ChangeEventSet>();
            for (Volume v : track.getVolumeList()) {
                vollist.addLast(createVolumeSet(v));
            }
            set.setVolumeSetList(vollist);

            var panlist = new LinkedList<ChangeEventSet>();
            for (Pan p : track.getPanList()) {
                panlist.addLast(createPanSet(p));
            }
            set.setPanSetList(panlist);

            list.add(set);
        }

        return list;
    }

    protected abstract TempoSet createTempoSet(Tempo t);

    protected abstract ChangeEventSet createInstrumentSet(Instrument i);

    protected abstract ChangeEventSet createVolumeSet(Volume v);

    protected abstract ChangeEventSet createPanSet(Pan p);

    private static void deleteInvalidControlCommands(Intermediate src, List<CommandPartSet> partSet) {
        try {
            int i = 0;
            for (Track track : src.getTrackList()) {
                Iterator<NoteRest> noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                NoteRest noteNode = noteNodeI.next();
                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> instNodeI = partSet.get(i).instrumentSetList.listIterator();
                ChangeEventSet instNode = instNodeI.next();

                while (noteNodeI.hasNext() && instNodeI.hasNext()) {
                    NoteRest curNote = noteNode;
                    ChangeEventSet curInst = instNode;

                    if (curNote.getEnd().compareTo(curInst.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (instNodeI.hasNext()) {
                            ChangeEventSet nextNode = instNodeI.next();
                            instNodeI.previous(); // rewind after peek
                            if (nextNode.getPosition().compareTo(curNote.getEnd()) > 0) {
                                newInstrumentSetList.addLast(curInst);
                            }
                        } else {
                            if (noteNodeI.hasNext()) {
                                newInstrumentSetList.addLast(curInst);
                            }
                        }
                    } else {
                        newInstrumentSetList.addLast(curInst);
                    }

                    instNode = instNodeI.next();
                }
                partSet.get(i).instrumentSetList = newInstrumentSetList;

                //
                noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                noteNode = noteNodeI.next();
                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> volNodeI = partSet.get(i).getVolumeSetList().listIterator();
                ChangeEventSet volNode = volNodeI.next();

                while (noteNode != null && volNodeI.hasNext()) {
                    NoteRest curNote = noteNode;
                    ChangeEventSet curVol = volNode;

                    if (curNote.getEnd().compareTo(curVol.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (volNodeI.hasNext()) {
                            ChangeEventSet nextVol = volNodeI.next();
                            volNodeI.previous(); // rewind after peek
                            if (nextVol.getPosition().compareTo(curNote.getEnd()) > 0) {
                                newVolumeSetList.addLast(curVol);
                            }
                        } else {
                            if (noteNodeI.hasNext()) {
                                newVolumeSetList.addLast(curVol);
                            }
                        }
                    } else {
                        newVolumeSetList.addLast(curVol);
                    }

                    volNode = volNodeI.next();
                }
                partSet.get(i).setVolumeSetList(newVolumeSetList);

                //
                noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                noteNode = noteNodeI.next();
                var newPansetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> panNodeI = partSet.get(i).getPanSetList().listIterator();
                ChangeEventSet panNode = panNodeI.next();

                while (noteNode != null && panNode != null) {
                    NoteRest curNote = noteNode;
                    ChangeEventSet curPan = panNode;

                    if (curNote.getEnd().compareTo(curPan.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
                        continue;
                    }

                    if (curNote instanceof Rest) {
                        if (panNodeI.hasNext()) {
                            ChangeEventSet nextPan = panNodeI.next();
                            panNodeI.previous(); // rewind after peek
                            if (nextPan.getPosition().compareTo(curNote.getEnd()) > 0) {
                                newPansetList.addLast(curPan);
                            }
                        } else {
                            if (noteNodeI.hasNext()) {
                                newPansetList.addLast(curPan);
                            }
                        }
                    } else {
                        newPansetList.addLast(curPan);
                    }

                    panNode = panNodeI.next();
                }
                partSet.get(i).panSetList = newPansetList;

                i++;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierRemoveUselessCommands"), ex);
        }
    }

    private static void modifySamePositionControlCommands(Intermediate src, LinkedList<TempoSet> tempoSetList, List<MusicDataModifier.CommandPartSet> partSet) {
        try {
            for (CommandPartSet set : partSet) {
                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curInstNodeI = set.instrumentSetList.listIterator();
                ChangeEventSet curInstNode = curInstNodeI.next();
                while (curInstNodeI.hasNext()) {
                    if (curInstNodeI.hasPrevious()) {
                        ChangeEventSet prevInstNode = curInstNodeI.previous();
                        curInstNodeI.next(); // rewind after peek
                        if (curInstNode.getPosition().compareTo(prevInstNode.getPosition()) == 0) {
                            newInstrumentSetList.removeLast();
                        }
                    }
                    newInstrumentSetList.addLast(curInstNode);
                    curInstNode = curInstNodeI.next();
                }
                set.instrumentSetList = newInstrumentSetList;

                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curVolNodeI = set.getVolumeSetList().listIterator();
                ChangeEventSet curVolNode = curVolNodeI.next();
                while (curVolNodeI.hasNext()) {
                    if (curVolNodeI.hasPrevious()) {
                        ChangeEventSet prevVolNode = curVolNodeI.previous();
                        curVolNodeI.next(); // rewind after peek
                        if (curVolNode.getPosition().compareTo(prevVolNode.getPosition()) == 0) {
                            newVolumeSetList.removeLast();
                        }
                    }
                    newVolumeSetList.addLast(curVolNode);
                    curVolNode = curVolNodeI.next();
                }
                set.setVolumeSetList(newVolumeSetList);

                var newPanSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curPanNodeI = set.getPanSetList().listIterator();
                ChangeEventSet curPanNode = curPanNodeI.next();
                while (curPanNodeI.hasNext()) {
                    if (curPanNodeI.hasPrevious()) {
                        ChangeEventSet prevPanNode = curPanNodeI.previous();
                        curPanNodeI.next(); // rewind after peek
                        if (curPanNode.getPosition().compareTo(prevPanNode.getPosition()) == 0) {
                            newPanSetList.removeLast();
                        }
                    }
                    newPanSetList.addLast(curPanNode);
                    curPanNode = curPanNodeI.next();
                }
                set.panSetList = newPanSetList;
            }

            var newTempoSetList = new LinkedList<TempoSet>();
            ListIterator<TempoSet> curTempoNodeI = tempoSetList.listIterator();
            TempoSet curTempoNode = curTempoNodeI.next();
            while (curTempoNodeI.hasNext()) {
                if (curTempoNodeI.hasPrevious()) {
                    TempoSet prevTempoNode = curTempoNodeI.previous();
                    curTempoNodeI.next(); // rewind after peek
                    if (curTempoNode.getPosition().compareTo(prevTempoNode.getPosition()) == 0) {
                        newTempoSetList.removeLast();
                    }
                }
                newTempoSetList.addLast(curTempoNode);
                curTempoNode = curTempoNodeI.next();
            }
            tempoSetList = newTempoSetList;
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierOptimizeSamePlaceType"), ex);
        }
    }

    private static void deletePredeclaredControlCommands(Intermediate src, LinkedList<TempoSet> tempoSetList, List<CommandPartSet> partSet, Settings settings, List<NotesStatus> statusList) {
        try {
            NotesStatus status = null;
            AtomicInteger i = new AtomicInteger(0);
            for (CommandPartSet set : partSet) {
                NotesStatus sts = statusList.stream().filter(x -> (x.getTrackNumber() == src.getTrackList().get(i.get()).getNumber() && x.getNumberInTrack() == src.getTrackList().get(i.get()).getNotesList().getFirst().getNumberInTrack())).findFirst().orElse(null);
                if (i.get() == 0) {
                    status = sts;
                }

                var newInstrumentSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curInstNodeI = set.getInstrumentSetList().listIterator();
                ChangeEventSet curInstNode = curInstNodeI.next();
                while (curInstNodeI.hasNext()) {
                    if (!curInstNodeI.hasPrevious()) {
                        newInstrumentSetList.addLast(curInstNode);
                    } else {
                        ChangeEventSet prevInstNode = curInstNodeI.previous();
                        curInstNodeI.next(); // rewind after peek
                        if (!curInstNode.toString(settings, sts.getSoundModule()).equals(prevInstNode.toString(settings, sts.getSoundModule()))) {
                            newInstrumentSetList.addLast(curInstNode);
                        }
                    }
                    curInstNode = curInstNodeI.next();
                }
                set.setInstrumentSetList(newInstrumentSetList);

                var newVolumeSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curVolNodeI = set.getVolumeSetList().listIterator();
                ChangeEventSet curVolNode = curVolNodeI.next();
                while (curVolNodeI.hasNext()) {
                    if (!curVolNodeI.hasPrevious()) {
                        newVolumeSetList.addLast(curVolNode);
                    } else {
                        ChangeEventSet prevVolNode = curVolNodeI.previous();
                        curVolNodeI.next(); // rewind after peek
                        if (!curVolNode.toString(settings, sts.getSoundModule()).equals(prevVolNode.toString(settings, sts.getSoundModule()))) {
                            newVolumeSetList.addLast(curVolNode);
                        }
                    }
                    curVolNode = curVolNodeI.next();
                }
                set.setVolumeSetList(newVolumeSetList);

                var newPanSetList = new LinkedList<ChangeEventSet>();
                ListIterator<ChangeEventSet> curPanNodeI = set.panSetList.listIterator();
                ChangeEventSet curPanNode = curPanNodeI.next();
                while (curPanNodeI.hasNext()) {
                    if (!curPanNodeI.hasPrevious()) {
                        newPanSetList.addLast(curPanNode);
                    } else {
                        ChangeEventSet prevPanNode = curPanNodeI.previous();
                        curPanNodeI.next(); // rewind after peek
                        if (!curPanNode.toString(settings, sts.getSoundModule()).equals(prevPanNode.toString(settings, sts.getSoundModule()))) {
                            newPanSetList.addLast(curPanNode);
                        }
                    }
                    curPanNode = curPanNodeI.next();
                }
                set.setPanSetList(newPanSetList);

                i.getAndIncrement();
            }

            var newTempoSetList = new LinkedList<TempoSet>();
            ListIterator<TempoSet> curTempoNodeI = tempoSetList.listIterator();
            TempoSet curTempoNode = curTempoNodeI.next();
            while (curTempoNodeI.hasNext()) {
                if (!curTempoNodeI.hasPrevious()) {
                    newTempoSetList.addLast(curTempoNode);
                } else {
                    TempoSet prevTempoNode = curTempoNodeI.previous();
                    curTempoNodeI.next(); // rewind after peek
                    if (!curTempoNode.toString(settings, status.getSoundModule()).equals(prevTempoNode.toString(settings, status.getSoundModule()))) {
                        newTempoSetList.addLast(curTempoNode);
                    }
                }
                curTempoNode = curTempoNodeI.next();
            }
            tempoSetList = newTempoSetList;
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierOptimizeSameTypeValue"), ex);
        }
    }

    private static Intermediate convertTempoSetList(Intermediate src, LinkedList<TempoSet> tempoSetList) {
        return new Intermediate(src.getTrackList(), src.getTimeSignatureList(), src.getKeySignatureList(), convertTempoSetList(tempoSetList), src.getTitle(), src.getCountsPerWholeNote());
    }

    private static Intermediate convertCommandPartSetList(Intermediate src, List<CommandPartSet> commandPartList) {
        var newTrackList = new ArrayList<Track>();

        int i = 0;
        for (Track track : src.getTrackList()) {
            CommandPartSet set = commandPartList.get(i);
            newTrackList.add(new Track(track.getNotesList(), convertInstrumentSetList(set.instrumentSetList), convertVolumeSetList(set.volumeSetList), convertPanSetList(set.panSetList), track.getNumber(), track.getLength(), track.getName()));
            i++;
        }

        return new Intermediate(newTrackList, src.getTimeSignatureList(), src.getKeySignatureList(), src.getTempoList(), src.getTitle(), src.getCountsPerWholeNote());
    }

    private static LinkedList<Tempo> convertTempoSetList(LinkedList<TempoSet> setList) {
        var list = new LinkedList<Tempo>();
        setList.forEach(x -> list.addLast(x.getData()));
        return list;
    }

    private static LinkedList<Instrument> convertInstrumentSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Instrument>();
        setList.forEach(x -> list.addLast((Instrument) x.getData()));
        return list;
    }

    private static LinkedList<Volume> convertVolumeSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Volume>();
        setList.forEach(x -> list.addLast((Volume) x.getData()));
        return list;
    }

    private static LinkedList<Pan> convertPanSetList(LinkedList<ChangeEventSet> setList) {
        var list = new LinkedList<Pan>();
        setList.forEach(x -> list.addLast((Pan) x.getData()));
        return list;
    }

    private static Intermediate cutNoteByControlCommands(Intermediate src) {
        try {
            for (Track track : src.getTrackList()) {
                Iterator<NoteRest> noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                NoteRest noteNode = noteNodeI.next();
                if (src.getTrackList().indexOf(track) == 0) {
                    Iterator<Tempo> tempoNodeI = src.getTempoList().iterator();
                    Tempo tempoNode = tempoNodeI.next();

                    while (tempoNodeI.hasNext()) {
                        NoteRest curNote = noteNode;
                        Tempo curTempo = tempoNode;

                        if (curNote.getEnd().compareTo(curTempo.getPosition()) <= 0) {
                            noteNode = noteNodeI.next();
                            continue;
                        }

                        if (curNote.getStart().compareTo(curTempo.getPosition()) < 0) {
                            NoteRest newNote;
                            if (curNote instanceof Note nt) {
                                newNote = new Note(curTempo.getPosition().clone(), nt.getEnd().clone(), nt.getKeyNumber(), nt.getVelocity());
                            } else {
                                var rst = (Rest) curNote;
                                newNote = new Rest(curTempo.getPosition().clone(), rst.getEnd().clone());
                            }
                            track.getNotesList().getFirst().getNoteList().add(track.getNotesList().getFirst().getNoteList().indexOf(noteNode), newNote);

                            curNote.setEnd(curTempo.getPosition().clone());
                            curNote.setTieFlag(true);
                        }

                        tempoNode = tempoNodeI.next();
                    }
                }

                noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                noteNode = noteNodeI.next();
                Iterator<Instrument> instNodeI = track.getInstrumentList().iterator();
                Instrument instNode = instNodeI.next();

                while (instNodeI.hasNext()) {
                    NoteRest curNote = noteNode;
                    Instrument curInst = instNode;

                    if (curNote.getEnd().compareTo(curInst.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
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
                        track.getNotesList().getFirst().getNoteList().add(track.getNotesList().getFirst().getNoteList().indexOf(noteNode), newNote);

                        curNote.setEnd(curInst.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    instNode = instNodeI.next();
                }

                noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                noteNode = noteNodeI.next();
                Iterator<Volume> volNodeI = track.getVolumeList().iterator();
                Volume volNode = volNodeI.next();

                while (volNodeI.hasNext()) {
                    NoteRest curNote = noteNode;
                    Volume curVol = volNode;

                    if (curNote.getEnd().compareTo(curVol.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
                        continue;
                    }

                    if (curNote.getStart().compareTo(curVol.getPosition()) < 0) {
                        NoteRest newNote;
                        if (curNote instanceof Note nt) {
                            newNote = new Note(curVol.getPosition().clone(), nt.getEnd().clone(), nt.getKeyNumber(), nt.getVelocity());
                        } else {
                            var rst = (Rest) curNote;
                            newNote = new Rest(curVol.getPosition().clone(), rst.getEnd().clone());
                        }
                        track.getNotesList().getFirst().getNoteList().add(track.getNotesList().getFirst().getNoteList().indexOf(noteNode), newNote);

                        curNote.setEnd(curVol.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    volNode = volNodeI.next();
                }

                noteNodeI = track.getNotesList().getFirst().getNoteList().iterator();
                noteNode = noteNodeI.next();
                Iterator<Pan> panNodeI = track.getPanList().iterator();
                Pan panNode = panNodeI.next();

                while (panNodeI.hasNext()) {
                    NoteRest curNote = noteNode;
                    Pan curPan = panNode;

                    if (curNote.getEnd().compareTo(curPan.getPosition()) <= 0) {
                        noteNode = noteNodeI.next();
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
                        track.getNotesList().getFirst().getNoteList().add(track.getNotesList().getFirst().getNoteList().indexOf(noteNode), newNote);

                        curNote.setEnd(curPan.getPosition().clone());
                        curNote.setTieFlag(true);
                    }

                    panNode = panNodeI.next();
                }
            }

            return src;
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierCutByCommands"), ex);
        }
    }

    private static Intermediate cutNoteByBar(Intermediate src) {
        try {
            for (Track track : src.getTrackList()) {
                ListIterator<NoteRest> noteNodeI = track.getNotesList().getFirst().getNoteList().listIterator();
                while (noteNodeI.hasNext()) {
                    NoteRest noteNode = noteNodeI.next();
                    if (noteNode.getStart().getBar() < noteNode.getEnd().getBar() &&
                            (noteNode.getEnd().getBar() != noteNode.getStart().getBar() + 1 || noteNode.getEnd().getTick() != 0)) {
                        NoteRest newNr;
                        var newPos = new Position(noteNode.getStart().getBar() + 1, 0);

                        if (noteNode instanceof Note note) {
                            newNr = new Note(newPos.clone(), note.getEnd().clone(), note.getKeyNumber(), note.getVelocity());
                        } else {
                            var rest = (Rest) noteNode;
                            newNr = new Rest(newPos.clone(), rest.getEnd().clone());
                        }
                        track.getNotesList().getFirst().getNoteList().add(noteNodeI.previousIndex(), newNr);

                        noteNode.setEnd(newPos.clone());
                        noteNode.setTieFlag(true);
                    }
                }
            }

            return src;
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorModifierCutByBar"), ex);
        }
    }
}
