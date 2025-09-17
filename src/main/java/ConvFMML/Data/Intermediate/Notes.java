/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate;

import java.util.LinkedList;

import ConvFMML.Data.Intermediate.Event.NoteRest;
import ConvFMML.Data.Intermediate.Event.TimeSignature;


public class Notes implements Cloneable {

    private LinkedList<NoteRest> noteList;
    private int numberInTrack;

    public LinkedList<NoteRest> getNoteList() {
        return noteList;
    }

    public void setNoteList(LinkedList<NoteRest> noteList) {
        this.noteList = noteList;
    }

    public int getNumberInTrack() {
        return numberInTrack;
    }

    public void setNumberInTrack(int numberInTrack) {
        this.numberInTrack = numberInTrack;
    }

    public Notes(LinkedList<NoteRest> noteList, int numberInTrack) {
        this.noteList = noteList;
        this.numberInTrack = numberInTrack;
    }

    public void ModifyPositionsByCountsPerWholenote(int curCountsPerWholeNote, int newCountsPerWholeNote, LinkedList<TimeSignature> tsList) {
        double ratio = (double) newCountsPerWholeNote / (double) curCountsPerWholeNote;
        var newList = new LinkedList<NoteRest>();

        for (NoteRest note : noteList) {
            note.setStart(Position.ConvertByTimeDivisionRatio(note.getStart(), ratio, tsList));
            note.setEnd(Position.ConvertByTimeDivisionRatio(note.getEnd(), ratio, tsList));

            if (note.getStart().compareTo(note.getEnd()) != 0) {
                newList.addLast(note);
            }
        }

        this.noteList = newList;
    }

    @Override
    public Notes clone() {
        var clone = (Notes) MemberwiseClone();

        clone.noteList = new LinkedList<>();
        for (NoteRest nt : noteList) {
            clone.noteList.addLast(nt.clone());
        }

        return clone;
    }
}
