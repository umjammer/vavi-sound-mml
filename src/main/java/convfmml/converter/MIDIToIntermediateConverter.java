/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import convfmml.Common.Key;
import convfmml.data.midi.event.EndOfTrack;
import convfmml.data.midi.event.Event;
import convfmml.data.midi.event.KeySignature;
import convfmml.data.midi.event.NoteOff;
import convfmml.data.midi.event.NoteOn;
import convfmml.data.midi.event.Pan;
import convfmml.data.midi.event.ProgramChange;
import convfmml.data.midi.event.SequenceTrackName;
import convfmml.data.midi.event.SetTempo;
import convfmml.data.midi.event.TimeSignature;
import convfmml.data.midi.event.Volume;
import convfmml.data.midi.MIDI;
import convfmml.data.midi.Track;
import convfmml.Tables;


public class MIDIToIntermediateConverter {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private static class DataSet {

        private List<convfmml.data.intermediate.Track> trackList;
        private LinkedList<convfmml.data.intermediate.event.TimeSignature> timeSignatureList;
        private LinkedList<convfmml.data.intermediate.event.KeySignature> keySignatureList;
        private LinkedList<convfmml.data.intermediate.event.Tempo> tempoList;
        private String title;

        public List<convfmml.data.intermediate.Track> getTrackList() {
            return trackList;
        }

        public void setTrackList(List<convfmml.data.intermediate.Track> trackList) {
            this.trackList = trackList;
        }

        public LinkedList<convfmml.data.intermediate.event.TimeSignature> getTimeSignatureList() {
            return timeSignatureList;
        }

        public void setTimeSignatureList(LinkedList<convfmml.data.intermediate.event.TimeSignature> timeSignatureList) {
            this.timeSignatureList = timeSignatureList;
        }

        public LinkedList<convfmml.data.intermediate.event.KeySignature> getKeySignatureList() {
            return keySignatureList;
        }

        public void setKeySignatureList(LinkedList<convfmml.data.intermediate.event.KeySignature> keySignatureList) {
            this.keySignatureList = keySignatureList;
        }

        public LinkedList<convfmml.data.intermediate.event.Tempo> getTempoList() {
            return tempoList;
        }

        public void setTempoList(LinkedList<convfmml.data.intermediate.event.Tempo> tempoList) {
            this.tempoList = tempoList;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public convfmml.data.intermediate.Intermediate convert(MIDI midi) {
        try {
            var set = new DataSet();
            set.setTrackList(new ArrayList<>());

            convertConductorTrack(midi.getTrackList().getFirst(), set, midi.getTimeDivision());
            for (int i = 1; i < midi.getTrackSize(); i++) {
                set.trackList.add(convertTrack(midi.getTrackList().get(i), set.getTimeSignatureList().listIterator(), i));
            }

            return new convfmml.data.intermediate.Intermediate(set.trackList, set.timeSignatureList, set.keySignatureList, set.tempoList, set.title, (midi.getTimeDivision() * 4));
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorConverterFailedToIntermediate"), ex);
        }
    }

    private static void convertConductorTrack(Track midiTrack, DataSet set, int timeDivision) {
        LinkedList<Event> events = midiTrack.getEventList();

        set.timeSignatureList = convertTimeSignature(events, timeDivision);

        var pos = new convfmml.data.intermediate.Position(1, 0);
        ListIterator<convfmml.data.intermediate.event.TimeSignature> tsNodeI = set.timeSignatureList.listIterator();

        var ksl = new LinkedList<convfmml.data.intermediate.event.KeySignature>();
        ksl.addFirst(new convfmml.data.intermediate.event.KeySignature(pos.clone(), Key.CMaj)); // Set default key signature
        var tl = new LinkedList<convfmml.data.intermediate.event.Tempo>();
        tl.addFirst(new convfmml.data.intermediate.event.Tempo(pos.clone(), 120)); // Set default tempo
        String title = "";
        var length = new convfmml.data.intermediate.Position(1, 0);

        for (Event ev : events) {
            pos = new convfmml.data.intermediate.Position(pos, ev.getDeltaTime(), tsNodeI);
            while (tsNodeI.hasNext() && pos.compareTo(tsNodeI.next().getPosition()) >= 0) { // Go to proper node
            }

            switch (ev) {
                case SetTempo st -> {
                    var newST = new convfmml.data.intermediate.event.Tempo(pos.clone(), (60000000 / st.getValue()));
                    if (tl.getLast().getPosition().compareTo(newST.getPosition()) == 0) {
                        tl.removeLast();
                    }
                    tl.addLast(newST);
                }
                case KeySignature ks -> {
                    var newKS = new convfmml.data.intermediate.event.KeySignature(pos.clone(), Tables.KeyTable[ks.minorFlagNumber][ks.signatureNumber]);
                    if (ksl.getLast().getPosition().compareTo(newKS.getPosition()) == 0) {
                        ksl.removeLast();
                    }
                    ksl.addLast(newKS);
                }
                case SequenceTrackName stn -> title = stn.getName();
                case EndOfTrack eot -> length = pos.clone();
                default -> {}
            }
        }

        set.trackList.add(new convfmml.data.intermediate.Track(
                new ArrayList<>(),
                new LinkedList<>(),
                new LinkedList<>(),
                new LinkedList<>(),
                0,
                length,
                "Conductor Track"
        ));
        set.tempoList = tl;
        set.keySignatureList = ksl;
        set.title = title;
    }

    private static LinkedList<convfmml.data.intermediate.event.TimeSignature> convertTimeSignature(LinkedList<Event> events, int timeDivision) {
        var pos = new convfmml.data.intermediate.Position(1, 0);

        var tsl = new LinkedList<convfmml.data.intermediate.event.TimeSignature>();
        tsl.addFirst(new convfmml.data.intermediate.event.TimeSignature(pos.clone(), pos.clone(), 4, 4, ((int) timeDivision * 4))); // Set defalt time signature (4/4)

        for (Event ev : events) {
            pos = new convfmml.data.intermediate.Position(pos, ev.getDeltaTime(), tsl.listIterator(tsl.size() - 1));

            if (ev instanceof TimeSignature newEv) {

                convfmml.data.intermediate.Position oldPos = pos.clone();
                if (pos.getTick() != 0) { // skip if pos(x : 0)
                    pos = new convfmml.data.intermediate.Position((pos.getBar() + 1), 0);
                }

                var newTS = new convfmml.data.intermediate.event.TimeSignature(pos.clone(), oldPos, newEv.getNumerator(), (1 << newEv.getDenominatorBitShift()), ((timeDivision >> (newEv.getDenominatorBitShift() - 2)) * newEv.getNumerator()));
                if (tsl.getLast().getPosition().compareTo(newTS.getPrevSignedPosition()) == 0) {
                    tsl.removeLast();
                }
                tsl.addLast(newTS);
            }
        }

        return tsl;
    }

    private static convfmml.data.intermediate.Track convertTrack(Track midiTrack, ListIterator<convfmml.data.intermediate.event.TimeSignature> tsNodeI, int trackNumber) {
        var pos = new convfmml.data.intermediate.Position(1, 0);

        var nl = new ArrayList<convfmml.data.intermediate.Notes>();
        var il = new LinkedList<convfmml.data.intermediate.event.Instrument>();
        var vl = new LinkedList<convfmml.data.intermediate.event.Volume>();
        var pl = new LinkedList<convfmml.data.intermediate.event.Pan>();
        String name = "";
        var length = new convfmml.data.intermediate.Position(1, 0);

        for (Event ev : midiTrack.getEventList()) {
            pos = new convfmml.data.intermediate.Position(pos, ev.getDeltaTime(), tsNodeI);
            while (tsNodeI.hasNext() && pos.compareTo(tsNodeI.next().getPosition()) >= 0) { // Go to proper node
            }

            switch (ev) {
                case NoteOn noteOn -> funcNoteOn(nl, pos.clone(), noteOn);
                case NoteOff noteOff -> funcNoteOff(nl, pos.clone(), noteOff);
                case ProgramChange inst -> il.addLast(new convfmml.data.intermediate.event.Instrument(pos.clone(), inst.getNumber()));
                case Volume vol -> vl.addLast(new convfmml.data.intermediate.event.Volume(pos.clone(), vol.value));
                case Pan pan -> pl.addLast(new convfmml.data.intermediate.event.Pan(pos.clone(), pan.value));
                case SequenceTrackName stn -> name = stn.getName();
                case EndOfTrack endOfTrack -> length = pos.clone();
                default -> {}
            }
        }

        return new convfmml.data.intermediate.Track(nl, il, vl, pl, trackNumber, length, name);
    }

    private static void funcNoteOn(List<convfmml.data.intermediate.Notes> nl, convfmml.data.intermediate.Position position, NoteOn ev) {
        for (convfmml.data.intermediate.Notes notes : nl) {
            LinkedList<convfmml.data.intermediate.event.NoteRest> list = notes.getNoteList();
            if (list.getLast().getEnd() != null) { // Check note overlapping
                list.addLast(new convfmml.data.intermediate.event.Note(position, null, ev.getNumber(), ev.getVelocity()));
                return;
            }
        }

        // Create new notelist
        var newList = new LinkedList<convfmml.data.intermediate.event.NoteRest>();
        newList.addFirst(new convfmml.data.intermediate.event.Note(position, null, ev.getNumber(), ev.getVelocity()));
        nl.add(new convfmml.data.intermediate.Notes(newList, nl.size()));
    }

    private static void funcNoteOff(List<convfmml.data.intermediate.Notes> nl, convfmml.data.intermediate.Position position, NoteOff ev) {
        for (convfmml.data.intermediate.Notes notes : nl) {
            convfmml.data.intermediate.event.Note note = (convfmml.data.intermediate.event.Note) notes.getNoteList().getLast();
            if (note.getEnd() == null && note.getKeyNumber() == ev.getNumber()) {
                note.setEnd(position);
                return;
            }
        }
    }
}
