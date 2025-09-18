/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import convfmml.data.midi.event.ControlChange;
import convfmml.data.midi.event.EndOfTrack;
import convfmml.data.midi.event.Event;
import convfmml.data.midi.event.KeySignature;
import convfmml.data.midi.event.MIDIEvent;
import convfmml.data.midi.event.MetaEvent;
import convfmml.data.midi.event.NoteOff;
import convfmml.data.midi.event.NoteOn;
import convfmml.data.midi.event.Pan;
import convfmml.data.midi.event.ProgramChange;
import convfmml.data.midi.event.SequenceTrackName;
import convfmml.data.midi.event.SetTempo;
import convfmml.data.midi.event.SysExEvent;
import convfmml.data.midi.event.TimeSignature;
import convfmml.data.midi.event.Volume;
import convfmml.data.midi.MIDI;
import convfmml.data.midi.Track;
import dotnet4j.io.FileAccess;
import dotnet4j.io.FileMode;
import dotnet4j.io.FileNotFoundException;
import dotnet4j.io.FileStream;


public class MIDIReader {

    static final ResourceBundle rb = ResourceBundle.getBundle("messages");

    private static class DataSet {   // Inner class for collecting data to make MIDI class

        private int format;
        private int trackSize;
        private int timeDivision;
        private List<Track> trackList;

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public int getTrackSize() {
            return trackSize;
        }

        public void setTrackSize(int trackSize) {
            this.trackSize = trackSize;
        }

        public int getTimeDivision() {
            return timeDivision;
        }

        public void setTimeDivision(int timeDivision) {
            this.timeDivision = timeDivision;
        }

        public List<Track> getTrackList() {
            return trackList;
        }

        public void setTrackList(List<Track> trackList) {
            this.trackList = trackList;
        }
    }

    private String midiPath;

    public String getMIDIPath() {
        return midiPath;
    }

    public void setMIDIPath(String midiPath) {
        this.midiPath = midiPath;
    }

    public MIDI read(String midiPath, int format) {
        MIDI data = read(midiPath);
        if (/* WAVE.data.Format != format && */ format == 1) {
            return ConvertToFormat1(data);
        } else {
            return data;
        }
    }

    public MIDI read(String midiPath) {
        this.midiPath = midiPath;
        return read();
    }

    public MIDI read() {
        byte[] bs = InputData();
        try {
            var set = new DataSet();
            ReadHeaderChunk(bs, set);
            ReadTrackChunk(bs, set);
            return new MIDI(set.trackList, set.format, set.trackSize, set.timeDivision);
        } catch (Exception ex) {
            throw new IllegalStateException(String.format(rb.getString("ErrorMIDIBroken"), midiPath), ex);
        }
    }

    private byte[] InputData() {
        try {
            try (var fs = new FileStream(midiPath, FileMode.Open, FileAccess.Read)) {
                byte[] bs = new byte[(int) fs.getLength()];
                fs.read(bs, 0, bs.length);
                return bs;
            }
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException(String.format(rb.getString("ErrorMIDIReadFailed"), midiPath), ex);
        }
    }

    private void ReadHeaderChunk(byte[] bs, DataSet set) {
        if (!new String(Arrays.copyOfRange(bs, 0, 4), StandardCharsets.US_ASCII).equals("MThd"))
            throw new IllegalArgumentException("Internal: Couldn't read 'MThd'");

        set.setFormat(LittleEndianConverter.toUInt16(bs, 8));
        set.setTrackSize(LittleEndianConverter.toUInt16(bs, 10));
        set.setTimeDivision(LittleEndianConverter.toInt16(bs, 12));
    }

    private void ReadTrackChunk(byte[] bs, DataSet set) {
        set.setTrackList(new ArrayList<>());

        int cur = 14;   // First track chunk byte
        for (int i = 0; i < set.trackSize; i++) {
            if (!new String(Arrays.copyOfRange(bs, cur, cur + 4), StandardCharsets.US_ASCII).equals("MTrk"))
                throw new IllegalArgumentException("Internal: Couldn't read 'MTrk'");
            cur += 4;

            int dataLength = (int) LittleEndianConverter.toUInt32(bs, cur);
            cur += 4;

            set.trackList.add(InstantiateEvent(Arrays.copyOfRange(bs, cur, cur + dataLength)));
            cur += dataLength;
        }
    }

    private Track InstantiateEvent(byte[] bs) {
        var eventList = new LinkedList<Event>();

        byte b, status = 0;
        for (int i = 0; i < bs.length; i++) {
            var deltaTime = new VariableLengthQuantity(bs, i);
            i += deltaTime.getBytesLength();

            if (((b = bs[i]) & 0x80) != 0) {  // skip when try running status
                status = b;
                i++;
            }

            switch (status & 0xf0) {
                case 0x80:  // Note Off
                    eventList.addLast(new NoteOff((int) deltaTime.getValue(), (status & 0x0f), bs[i], bs[i + 1]));
                    i++;
                    break;

                case 0x90:  // Note On
                    if (bs[i + 1] == 0)   // 0x90 Note Off (velocity: 0)
                    {
                        eventList.addLast(new NoteOff((int) deltaTime.getValue(), (status & 0x0f), bs[i], bs[i + 1]));
                    } else {
                        eventList.addLast(new NoteOn((int) deltaTime.getValue(), (status & 0x0f), bs[i], bs[i + 1]));
                    }
                    i++;
                    break;

                case 0xa0:  // Polyphonic Key Pressure
                    eventList.addLast(new MIDIEvent((int) deltaTime.getValue(), (status & 0x0f)));
                    i++;
                    break;

                case 0xb0:  // Control Change
                    b = bs[i++];
                    switch (b) {
                        case 0x07:  // Volume
                            eventList.addLast(new Volume((int) deltaTime.getValue(), (status & 0x0f), bs[i]));
                            break;
                        case 0x0a:  // Pan
                            eventList.addLast(new Pan((int) deltaTime.getValue(), (status & 0x0f), bs[i]));
                            break;
                        default:    // Other Control Change
                            eventList.addLast(new ControlChange((int) deltaTime.getValue(), (status & 0x0f), bs[i]));
                            break;
                    }
                    break;

                case 0xc0:  // Program Change
                    eventList.addLast(new ProgramChange((int) deltaTime.getValue(), (status & 0x0f), bs[i]));
                    break;

                case 0xd0:  // Channel Pressure
                    eventList.addLast(new MIDIEvent((int) deltaTime.getValue(), (status & 0x0f)));
                    break;

                case 0xe0:  // Pitch Bend
                    eventList.addLast(new MIDIEvent((int) deltaTime.getValue(), (status & 0x0f)));
                    i++;
                    break;

                case 0xf0:
                    switch (status & 0xff) {
                        case 0xf0:
                        case 0xf7:  // SysEx Event
                        {
                            var vlq = new VariableLengthQuantity(bs, i);
                            i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                            eventList.addLast(new SysExEvent((int) deltaTime.getValue()));
                            break;
                        }

                        case 0xff:  // Meta Event
                            b = bs[i++];
                            switch (b) {
                                case 0x01:  // Text Event
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x02:  // Copyright Notice
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x03:  // Sequence/Track Name
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength();   // Value 0 has 1 byte
                                    String text;
                                    if (vlq.getValue() == 0) {
                                        text = "";
                                    } else {
                                        text = new String(Arrays.copyOfRange(bs, i, (int) (i + vlq.getValue())), Charset.forName("shift_jis"));
                                    }
                                    eventList.addLast(new SequenceTrackName((int) deltaTime.getValue(), text));
                                    i += (int) vlq.getValue() - 1;    // Unmove cursor if value 0, else jump to last byte
                                    break;
                                }

                                case 0x04:  // Instrument Name
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x05:  // Lyric
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x06:  // Marker
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x07:  // Cue Point
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x08:  // Program Name
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x09:  // Device Name
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                case 0x20:  // MIDI Channel Prefix
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    i++;
                                    break;

                                case 0x21:  // MIDI Port Prefix
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    i++;
                                    break;

                                case 0x2f:  // End of Track
                                    eventList.addLast(new EndOfTrack((int) deltaTime.getValue()));
                                    break;

                                case 0x51:  // Set Tempo
                                    i++;
                                    int tempoValue = 0;
                                    for (int j = 0; j < 3; j++) {
                                        tempoValue <<= 8;
                                        tempoValue |= bs[i + j];
                                    }
                                    eventList.addLast(new SetTempo((int) deltaTime.getValue(), tempoValue));
                                    i += 2;
                                    break;

                                case 0x54:  // SMTPE Offset
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    i += 5;
                                    break;

                                case 0x58:  // Time Signature
                                    i++;
                                    eventList.addLast(new TimeSignature((int) deltaTime.getValue(), bs[i], bs[i + 1], bs[i + 2], bs[i + 3]));
                                    i += 3;
                                    break;

                                case 0x59:  // Key Signature
                                    i++;
                                    eventList.addLast(new KeySignature((int) deltaTime.getValue(), (byte) bs[i], bs[i + 1]));
                                    i++;
                                    break;

                                case 0x7f:  // Sequencer Specific Meta Event
                                {
                                    var vlq = new VariableLengthQuantity(bs, i);
                                    i += vlq.getBytesLength() + (int) vlq.getValue() - 1;  // Jump to last byte
                                    eventList.addLast(new MetaEvent((int) deltaTime.getValue()));
                                    break;
                                }

                                default:
                                    throw new IllegalArgumentException("Internal: Unknown Meta Event");
                            }
                            break;

                        default:
                            throw new IllegalArgumentException("Internal: Unknown 0xfx Event");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Internal: Unknown Event");
            }
        }   // for

        return new Track(eventList);
    }

    private MIDI ConvertToFormat1(MIDI src) {
        try {
            Track srcTrack = src.getTrackList().get(0);
            var newTracks = new ArrayList<Track>();
            int cnt = 0;        // event counter

            var eventlist = new LinkedList<Event>();
            int deltaTime = 0;


            // Create Conductor track
            for (Event ev : srcTrack.getEventList()) {
                deltaTime += ev.getDeltaTime();

                if (ev instanceof MetaEvent) {
                    MetaEvent modEv;
                    if (ev instanceof SetTempo) {
                        var st = (SetTempo) ev;
                        modEv = new SetTempo(deltaTime, st.value);
                    } else if (ev instanceof TimeSignature) {
                        var ts = (TimeSignature) ev;
                        modEv = new TimeSignature(deltaTime, ts.getNumerator(), ts.getDenominatorBitShift(), ts.getMIDIClockPerMetronomeTick(), ts.getNumberOfNotesPerClocks());
                    } else if (ev instanceof KeySignature) {
                        var ks = (KeySignature) ev;
                        modEv = new KeySignature(deltaTime, ks.signatureNumber, ks.minorFlagNumber);
                    } else if (ev instanceof SequenceTrackName) {
                        var stn = (SequenceTrackName) ev;
                        modEv = new SequenceTrackName(deltaTime, stn.getName());
                    } else if (ev instanceof EndOfTrack) {
                        modEv = new EndOfTrack(deltaTime);
                    } else {
                        modEv = new MetaEvent(deltaTime);
                    }
                    eventlist.addLast(modEv);

                    deltaTime = 0;

                    if (!(ev instanceof EndOfTrack)) {
                        cnt++;
                    }
                }
            }
            newTracks.add(new Track(eventlist));

            eventlist = new LinkedList<Event>();
            deltaTime = 0;


            // Create System Setup track
            for (Event ev : srcTrack.getEventList()) {
                deltaTime += ev.getDeltaTime();

                if (ev instanceof SysExEvent) {
                    eventlist.addLast(new SysExEvent(deltaTime));

                    deltaTime = 0;
                    cnt++;
                } else if (ev instanceof EndOfTrack) {
                    eventlist.addLast(new EndOfTrack(deltaTime));
                }
            }
            newTracks.add(new Track(eventlist));


            // Create Notes track
            for (int ch = 0; cnt + 1 < srcTrack.getEventList().size(); ch++) {
                eventlist = new LinkedList<Event>();
                deltaTime = 0;

                for (Event ev : srcTrack.getEventList()) {
                    deltaTime += ev.getDeltaTime();

                    if (ev instanceof MIDIEvent midiEv) {
                        if (midiEv.getChannel() == ch) {
                            MIDIEvent modEv;
                            if (midiEv instanceof NoteOn nton) {
                                modEv = new NoteOn(deltaTime, nton.getChannel(), nton.getNumber(), nton.getVelocity());
                            } else if (midiEv instanceof NoteOff ntoff) {
                                modEv = new NoteOff(deltaTime, ntoff.getChannel(), ntoff.getNumber(), ntoff.getVelocity());
                            } else if (midiEv instanceof ProgramChange pc) {
                                modEv = new ProgramChange(deltaTime, pc.getChannel(), pc.getNumber());
                            } else if (midiEv instanceof Volume vol) {
                                modEv = new Volume(deltaTime, vol.getChannel(), vol.getValue());
                            } else if (midiEv instanceof Pan pan) {
                                modEv = new Pan(deltaTime, pan.getChannel(), pan.getValue());
                            } else if (midiEv instanceof ControlChange cc) {
                                modEv = new ControlChange(deltaTime, cc.getChannel(), cc.value);
                            } else {
                                modEv = new MIDIEvent(deltaTime, midiEv.getChannel());
                            }
                            eventlist.addLast(modEv);

                            deltaTime = 0;
                            cnt++;
                        }
                    } else if (ev instanceof EndOfTrack) {
                        eventlist.addLast(new EndOfTrack(deltaTime));
                    }
                }
                newTracks.add(new Track(eventlist));
            }


            return new MIDI(newTracks, 1, newTracks.size(), src.getTimeDivision());
        } catch (Exception ex) {
            throw new IllegalArgumentException(rb.getString("ErrorMIDIFormat1"), ex);
        }
    }
}
