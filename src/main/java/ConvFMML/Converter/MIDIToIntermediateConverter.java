/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ConvFMML.Common.Key;
import ConvFMML.Data.Intermediate.Event.TimeSignature;
import ConvFMML.Data.Intermediate.Track;
import ConvFMML.Data.MIDI.Event.EndOfTrack;
import ConvFMML.Data.MIDI.Event.Event;
import ConvFMML.Data.MIDI.Event.KeySignature;
import ConvFMML.Data.MIDI.Event.NoteOff;
import ConvFMML.Data.MIDI.Event.NoteOn;
import ConvFMML.Data.MIDI.Event.Pan;
import ConvFMML.Data.MIDI.Event.ProgramChange;
import ConvFMML.Data.MIDI.Event.SequenceTrackName;
import ConvFMML.Data.MIDI.Event.SetTempo;
import ConvFMML.Data.MIDI.Event.Volume;
import ConvFMML.Data.MIDI.MIDI;
import ConvFMML.Tables;


public class MIDIToIntermediateConverter {

    private static class DataSet {

        private List<Track> trackList;
        private LinkedList<TimeSignature> timeSignatureList;
        private LinkedList<ConvFMML.Data.Intermediate.Event.KeySignature> keySignatureList;
        private LinkedList<ConvFMML.Data.Intermediate.Event.Tempo> tempoList;
        private String title;

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

        public LinkedList<ConvFMML.Data.Intermediate.Event.KeySignature> getKeySignatureList() {
            return keySignatureList;
        }

        public void setKeySignatureList(LinkedList<ConvFMML.Data.Intermediate.Event.KeySignature> keySignatureList) {
            this.keySignatureList = keySignatureList;
        }

        public LinkedList<ConvFMML.Data.Intermediate.Event.Tempo> getTempoList() {
            return tempoList;
        }

        public void setTempoList(LinkedList<ConvFMML.Data.Intermediate.Event.Tempo> tempoList) {
            this.tempoList = tempoList;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public ConvFMML.Data.Intermediate.Intermediate Convert(MIDI midi) {
        try {
            var set = new DataSet();
            set.setTrackList(new ArrayList<Track>());

            ConvertConductorTrack(midi.getTrackList().get(0), set, midi.getTimeDivision());
            for (int i = 1; i < midi.getTrackSize(); i++) {
                set.trackList.add(ConvertTrack(midi.getTrackList().get(i), set.getTimeSignatureList().iterator(), i));
            }

            return new ConvFMML.Data.Intermediate.Intermediate(set.trackList, set.timeSignatureList, set.keySignatureList, set.tempoList, set.title, (midi.getTimeDivision() * 4));
        } catch (Exception ex) {
            throw new Exception(Resources.ErrorConverterFailedToIntermediate, ex);
        }
    }

    private void ConvertConductorTrack(Track midiTrack, DataSet set, int timeDivision) {
        LinkedList<Event> events = midiTrack.EventList;

        set.timeSignatureList = ConvertTimeSignature(events, timeDivision);

        var pos = new ConvFMML.Data.Intermediate.Position(1, 0);
        Iterator<ConvFMML.Data.Intermediate.Event.TimeSignature> tsNode = set.timeSignatureList.iterator();

        var ksl = new LinkedList<ConvFMML.Data.Intermediate.Event.KeySignature>();
        ksl.addFirst(new ConvFMML.Data.Intermediate.Event.KeySignature(pos.clone(), Key.CMaj));      // Set default key signature
        var tl = new LinkedList<ConvFMML.Data.Intermediate.Event.Tempo>();
        tl.addFirst(new ConvFMML.Data.Intermediate.Event.Tempo(pos.clone(), 120));       // Set default tempo
        String title = "";
        var length = new ConvFMML.Data.Intermediate.Position(1, 0);

        for (Event ev : events) {
            pos = new ConvFMML.Data.Intermediate.Position(pos, ev.getDeltaTime(), tsNode);
            while (tsNode.Next != null && pos.compareTo(tsNode.Next.Value.Position) >= 0)       // Go to proper node
            {
                tsNode = tsNode.Next;
            }

            if (ev instanceof SetTempo) {
                var st = (SetTempo) ev;
                var newST = new ConvFMML.Data.Intermediate.Event.Tempo(pos.clone(), (60000000 / st.Value));
                if (tl.getLast().getValue().Position.compareTo(newST.getPosition()) == 0) {
                    tl.removeLast();
                }
                tl.addLast(newST);
            } else if (ev instanceof KeySignature) {
                var ks = (KeySignature) ev;
                var newKS = new ConvFMML.Data.Intermediate.Event.KeySignature(pos.clone(), Tables.KeyTable[ks.minorFlagNumber][ks.signatureNumber]);
                if (ksl.getLast().Value.Position.compareTo(newKS.getPosition()) == 0) {
                    ksl.removeLast();
                }
                ksl.addLast(newKS);
            } else if (ev instanceof SequenceTrackName) {
                var stn = (SequenceTrackName) ev;
                title = stn.Name;
            } else if (ev instanceof EndOfTrack) {
                length = pos.clone();
            }
        }

        set.trackList.add(new ConvFMML.Data.Intermediate.Track(
                new ArrayList<ConvFMML.Data.Intermediate.Notes>(),
                new LinkedList<ConvFMML.Data.Intermediate.Event.Instrument>(),
                new LinkedList<ConvFMML.Data.Intermediate.Event.Volume>(),
                new LinkedList<ConvFMML.Data.Intermediate.Event.Pan>(),
                0,
                length,
                "Conductor Track"
        ));
        set.tempoList = tl;
        set.keySignatureList = ksl;
        set.title = title;
    }

    private LinkedList<ConvFMML.Data.Intermediate.Event.TimeSignature> ConvertTimeSignature(LinkedList<Event> events, int timeDivision) {
        var pos = new ConvFMML.Data.Intermediate.Position(1, 0);

        var tsl = new LinkedList<ConvFMML.Data.Intermediate.Event.TimeSignature>();
        tsl.addFirst(new ConvFMML.Data.Intermediate.Event.TimeSignature(pos.clone(), pos.clone(), 4, 4, ((int) timeDivision * 4)));    // Set defalt time signature (4/4)

        for (Event ev : events) {
            pos = new ConvFMML.Data.Intermediate.Position(pos, ev.getDeltaTime(), tsl.getLast());

            if (ev instanceof TimeSignature) {
                var newEv = (TimeSignature) ev;

                ConvFMML.Data.Intermediate.Position oldPos = pos.clone();
                if (pos.Tick != 0)  // skip if pos(x : 0)
                {
                    pos = new ConvFMML.Data.Intermediate.Position((pos.getBar() + 1), 0);
                }

                var newTS = new ConvFMML.Data.Intermediate.Event.TimeSignature(pos.clone(), oldPos, newEv.getNumerator(), (1 << newEv.DenominatorBitShift), ((int) (timeDivision >> (newEv.DenominatorBitShift - 2)) * (int) newEv.Numerator));
                if (tsl.getLast().Value.Position.compareTo(newTS.getPrevSignedPosition()) == 0) {
                    tsl.removeLast();
                }
                tsl.addLast(newTS);
            }
        }

        return tsl;
    }

    private ConvFMML.Data.Intermediate.Track ConvertTrack(Track midiTrack, Iterator<TimeSignature> tsNode, int trackNumber) {
        var pos = new ConvFMML.Data.Intermediate.Position(1, 0);

        var nl = new ArrayList<ConvFMML.Data.Intermediate.Notes>();
        var il = new LinkedList<ConvFMML.Data.Intermediate.Event.Instrument>();
        var vl = new LinkedList<ConvFMML.Data.Intermediate.Event.Volume>();
        var pl = new LinkedList<ConvFMML.Data.Intermediate.Event.Pan>();
        String name = "";
        var length = new ConvFMML.Data.Intermediate.Position(1, 0);

        for (Event ev : midiTrack.EventList) {
            pos = new ConvFMML.Data.Intermediate.Position(pos, ev.getDeltaTime(), tsNode);
            while (tsNode.Next != null && pos.compareTo(tsNode.Next.Value.Position) >= 0)       // Go to proper node
            {
                tsNode = tsNode.Next;
            }

            if (ev instanceof NoteOn) {
                FuncNoteOn(nl, pos.clone(), (NoteOn) ev);
            } else if (ev instanceof NoteOff) {
                FuncNoteOff(nl, pos.clone(), (NoteOff) ev);
            } else if (ev instanceof ProgramChange) {
                var inst = (ProgramChange) ev;
                il.addLast(new ConvFMML.Data.Intermediate.Event.Instrument(pos.clone(), inst.Number));
            } else if (ev instanceof Volume) {
                var vol = (Volume) ev;
                vl.addLast(new ConvFMML.Data.Intermediate.Event.Volume(pos.clone(), vol.value));
            } else if (ev instanceof Pan) {
                var pan = (Pan) ev;
                pl.addLast(new ConvFMML.Data.Intermediate.Event.Pan(pos.clone(), pan.value));
            } else if (ev instanceof SequenceTrackName) {
                var stn = (SequenceTrackName) ev;
                name = stn.Name;
            } else if (ev instanceof EndOfTrack) {
                length = pos.clone();
            }
        }

        return new ConvFMML.Data.Intermediate.Track(nl, il, vl, pl, trackNumber, length, name);
    }

    private void FuncNoteOn(List<ConvFMML.Data.Intermediate.Notes> nl, ConvFMML.Data.Intermediate.Position position, NoteOn ev) {
        for (ConvFMML.Data.Intermediate.Notes notes : nl) {
            LinkedList<ConvFMML.Data.Intermediate.Event.NoteRest> list = notes.getNoteList();
            if (list.getLast().getEnd() != null)        // Check note overlapping
            {
                list.addLast(new ConvFMML.Data.Intermediate.Event.Note(position, null, ev.Number, ev.Velocity));
                return;
            }
        }

        // Create new notelist
        var newList = new LinkedList<ConvFMML.Data.Intermediate.Event.NoteRest>();
        newList.addFirst(new ConvFMML.Data.Intermediate.Event.Note(position, null, ev.Number, ev.Velocity));
        nl.add(new ConvFMML.Data.Intermediate.Notes(newList, nl.size()));
    }

    private void FuncNoteOff(List<ConvFMML.Data.Intermediate.Notes> nl, ConvFMML.Data.Intermediate.Position position, NoteOff ev) {
        for (ConvFMML.Data.Intermediate.Notes notes : nl) {
            ConvFMML.Data.Intermediate.Event.Note note = (ConvFMML.Data.Intermediate.Event.Note) notes.NoteList.Last.Value;
            if (note.getEnd() == null && note.getKeyNumber() == ev.Number) {
                note.setEnd(position);
                return;
            }
        }
    }
}
