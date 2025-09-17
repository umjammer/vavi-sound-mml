/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ConvFMML.Data.Intermediate.Event.Instrument;
import ConvFMML.Data.Intermediate.Event.NoteRest;
import ConvFMML.Data.Intermediate.Event.Pan;
import ConvFMML.Data.Intermediate.Event.Volume;
import ConvFMML.Data.Intermediate.Event.TimeSignature;


public class Track implements Cloneable {

    private List<Notes> notesList;
    private LinkedList<Instrument> instrumentList;
    private LinkedList<Volume> volumeList;
    private LinkedList<Pan> panList;
    private int number;
    private Position length;
    private String name;

    public List<Notes> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Notes> notesList) {
        this.notesList = notesList;
    }

    public LinkedList<Instrument> getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(LinkedList<Instrument> instrumentList) {
        this.instrumentList = instrumentList;
    }

    public LinkedList<Volume> getVolumeList() {
        return volumeList;
    }

    public void setVolumeList(LinkedList<Volume> volumeList) {
        this.volumeList = volumeList;
    }

    public LinkedList<Pan> getPanList() {
        return panList;
    }

    public void setPanList(LinkedList<Pan> panList) {
        this.panList = panList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Position getLength() {
        return length;
    }

    public void setLength(Position length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return notesList != null ? notesList.size() : 0;
    }

    public Track(
            List<Notes> notesList,
            LinkedList<Instrument> instrumentList,
            LinkedList<Volume> volumeList,
            LinkedList<Pan> panList,
            int number,
            Position length,
            String name
    ) {
        this.notesList = notesList;
        this.instrumentList = instrumentList;
        this.volumeList = volumeList;
        this.panList = panList;
        this.number = number;
        this.length = length;
        this.name = name;
    }

    public void ModifyPositionsByCountsPerWholenote(int curCountsPerWholeNote, int newCountsPerWholeNote, LinkedList<TimeSignature> tsList) {
        double ratio = (double) newCountsPerWholeNote / (double) curCountsPerWholeNote;

        for (Instrument inst : this.instrumentList) {
            inst.setPosition(Position.ConvertByTimeDivisionRatio(inst.getPosition(), ratio, tsList));
        }

        for (Volume vol : this.volumeList) {
            vol.setPosition(Position.ConvertByTimeDivisionRatio(vol.getPosition(), ratio, tsList));
        }

        for (Pan pan : this.panList) {
            pan.setPosition(Position.ConvertByTimeDivisionRatio(pan.getPosition(), ratio, tsList));
        }

        List<NoteRest> tempList = new ArrayList<>();
        for (Notes ns : this.notesList) {
            ns.ModifyPositionsByCountsPerWholenote(curCountsPerWholeNote, newCountsPerWholeNote, tsList);
            tempList.addAll(ns.getNoteList());
        }
        tempList = tempList.sort(x -> x.getStart()).ToList();

        List<Notes> newNotesList = new ArrayList<>();
        for (NoteRest addNote : tempList) {
            ReorderNotes(newNotesList, addNote);
        }
        this.notesList = newNotesList;
    }

    private void ReorderNotes(List<Notes> newNotesList, NoteRest addNote) {
        for (Notes notes : newNotesList) {
            LinkedList<NoteRest> listInNotes = notes.getNoteList();
            if (listInNotes.getLast().Value.End.compareTo(addNote.getStart()) <= 0) {
                listInNotes.addLast(addNote);
                return;
            }
        }

        var newListInNotes = new LinkedList<NoteRest>();
        newListInNotes.addLast(addNote);
        newNotesList.add(new Notes(newListInNotes, newNotesList.size()));
    }

    public List<NotesStatus> GetPartStatusList() {
        var list = new ArrayList<NotesStatus>();
        if (notesList.isEmpty()) {
            list.add(new NotesStatus(number, name, 0, true));
        } else {
            for (Notes notes : notesList) {
                list.add(new NotesStatus(number, name, notes.getNumberInTrack(), false));
            }
        }
        return list;
    }

    public Object clone() {
        var clone = (Track) MemberwiseClone();

        clone.notesList = new ArrayList<Notes>();
        notesList.forEach(x -> clone.notesList.add(x.clone()));

        clone.instrumentList = new LinkedList<Instrument>();
        for (Instrument inst : instrumentList) {
            clone.instrumentList.addLast(inst.clone());
        }

        clone.volumeList = new LinkedList<Volume>();
        for (Volume vol : volumeList) {
            clone.volumeList.addLast(vol.clone());
        }

        clone.panList = new LinkedList<Pan>();
        for (Pan pan : panList) {
            clone.panList.addLast(pan.clone());
        }

        return clone;
    }
}
