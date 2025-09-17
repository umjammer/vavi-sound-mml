/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ConvFMML.Data.Intermediate.Event.Tempo;
import ConvFMML.Data.Intermediate.Event.KeySignature;
import ConvFMML.Data.Intermediate.Event.TimeSignature;


public class Intermediate implements Cloneable {

    private List<Track> trackList;
    private LinkedList<TimeSignature> timeSignatureList;
    private LinkedList<KeySignature> keySignatureList;
    private LinkedList<Tempo> tempoList;
    private String title;
    private int countsPerWholeNote;

    public List<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    public LinkedList<TimeSignature> getTimeSignatureList() {
        return timeSignatureList;
    }

    public void setTimeSignatureList(LinkedList<TimeSignature> timeSignatureList) {
        this.timeSignatureList = timeSignatureList;
    }

    public LinkedList<KeySignature> getKeySignatureList() {
        return keySignatureList;
    }

    public void setKeySignatureList(LinkedList<KeySignature> keySignatureList) {
        this.keySignatureList = keySignatureList;
    }

    public LinkedList<Tempo> getTempoList() {
        return tempoList;
    }

    public void setTempoList(LinkedList<Tempo> tempoList) {
        this.tempoList = tempoList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCountsPerWholeNote() {
        return countsPerWholeNote;
    }

    public void setCountsPerWholeNote(int value) {
        int prev = this.countsPerWholeNote;
        this.countsPerWholeNote = value;
        if (prev != 0) {
            ModifyPositionsByCountsPerWholenote(prev, value);
        }
    }

    public Intermediate(
            List<Track> trackList,
            LinkedList<TimeSignature> timeSignatureList,
            LinkedList<KeySignature> keysignatureList,
            LinkedList<Tempo> tempoList,
            String title,
            int countsPerWholeNote
    ) {
        this.trackList = trackList;
        this.timeSignatureList = timeSignatureList;
        this.keySignatureList = keysignatureList;
        this.tempoList = tempoList;
        this.title = title;
        this.countsPerWholeNote = countsPerWholeNote;
    }

    public List<NotesStatus> GetNotesStatusList() {
        var list = new ArrayList<NotesStatus>();
        for (Track track : this.trackList) {
            list.addAll(track.GetPartStatusList());
        }
        return list;
    }

    public void ModifyPositionsByCountsPerWholenote(int countsPerWholeNote) {
        ModifyPositionsByCountsPerWholenote(this.countsPerWholeNote, countsPerWholeNote);
    }

    private void ModifyPositionsByCountsPerWholenote(int curCountsPerWholeNote, int newCountsPerWholeNote) {
        double ratio = (double) newCountsPerWholeNote / (double) curCountsPerWholeNote;

        for (TimeSignature ts : this.timeSignatureList) {
            ts.setTickPerBar((int) (ts.getTickPerBar() * ratio));
            ts.setPosition(Position.ConvertByTimeDivisionRatio(ts.getPosition(), ratio, null));
            ts.setPrevSignedPosition(Position.ConvertByTimeDivisionRatio(ts.getPrevSignedPosition(), ratio, timeSignatureList));
        }

        for (KeySignature ks : keySignatureList) {
            ks.setPosition(Position.ConvertByTimeDivisionRatio(ks.getPosition(), ratio, timeSignatureList));
        }

        for (Tempo tp : tempoList) {
            tp.setPosition(Position.ConvertByTimeDivisionRatio(tp.getPosition(), ratio, timeSignatureList));
        }

        for (Track trk : trackList) {
            trk.ModifyPositionsByCountsPerWholenote(curCountsPerWholeNote, newCountsPerWholeNote, timeSignatureList);
        }
    }

    public Object clone() {
        var clone = (Intermediate) MemberwiseClone();

        clone.trackList = new ArrayList<Track>();
        trackList.forEach(x -> clone.trackList.add(x.clone()));

        clone.timeSignatureList = new LinkedList<TimeSignature>();
        for (TimeSignature ts : timeSignatureList) {
            clone.timeSignatureList.addLast(ts.clone());
        }

        clone.keySignatureList = new LinkedList<KeySignature>();
        for (KeySignature ks : keySignatureList) {
            clone.keySignatureList.addLast(ks.clone());
        }

        clone.tempoList = new LinkedList<Tempo>();
        for (Tempo tp : tempoList) {
            clone.tempoList.addLast(tp.clone());
        }

        return clone;
    }
}
