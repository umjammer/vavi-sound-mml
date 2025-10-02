/*
 * https://github.com/korinVR/mml2smf/blob/main/src/mml2smf.mjs
 *
 * https://github.com/korinVR/mml2smf/blob/main/LICENSE
 */

package mml2smf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

import static mml2smf.RhinoMapper.fromNativeObject;


/**
 * Mml2Smf.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-09-20 nsano initial version <br>
 */
public class Mml2Smf {

    public static class Token {

        private String command;
        private String tone;
        private String accidentals;
        private int length;
        private String dots;
        private int number;
        private int quantity;
        private int value;
        private int channel;

        @JSGetter
        public String getCommand() {
            return command;
        }

        @JSSetter
        public void setCommand(String command) {
            this.command = command;
        }

        @JSGetter
        public String getTone() {
            return tone;
        }

        @JSSetter
        public void setTone(String tone) {
            this.tone = tone;
        }

        @JSGetter
        public String getAccidentals() {
            return accidentals;
        }

        @JSSetter
        public void setAccidentals(String accidentals) {
            this.accidentals = accidentals;
        }

        @JSGetter
        public int getLength() {
            return length;
        }

        @JSSetter
        public void setLength(int length) {
            this.length = length;
        }

        @JSGetter
        public String getDots() {
            return dots;
        }

        @JSSetter
        public void setDots(String dots) {
            this.dots = dots;
        }

        @JSGetter
        public int getNumber() {
            return number;
        }

        @JSSetter
        public void setNumber(int number) {
            this.number = number;
        }

        @JSGetter
        public int getQuantity() {
            return quantity;
        }

        @JSSetter
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @JSGetter
        public int getValue() {
            return value;
        }

        @JSSetter
        public void setValue(int value) {
            this.value = value;
        }

        @JSGetter
        public int getChannel() {
            return channel;
        }

        @JSSetter
        public void setChannel(int channel) {
            this.channel = channel;
        }
    }

    public interface Parser extends br.ufmg.dcc.nanocomp.peg.Parser<NativeArray> {
        NativeArray parse(String mml) throws MMLParseException;
    }

    public static class MMLParseException extends Exception {
        public MMLParseException(String message) { super(message); }
    }

    public static byte[] convert(String mml, int timebase, Parser parser) throws MMLParseException {
        int startTick = 0;
        NativeArray tracks = parser.parse(mml + ";");

        List<List<Integer>> trackDataArray = new ArrayList<>();
        int channel = 0;

        for (Object o : tracks) {
            NativeArray track = (NativeArray) o;
            trackDataArray.add(createTrackData(track, timebase, channel));
            channel++;
            if (channel > 15) throw new MMLParseException("Exceeded maximum MIDI channel (16)");
        }

        int format = tracks.size() > 1 ? 1 : 0;
        List<Integer> smf = new ArrayList<>();

        // Header chunk
        smf.addAll(Arrays.asList(0x4d, 0x54, 0x68, 0x64)); // "MThd"
        write4bytes(smf, 6); // header length
        write2bytes(smf, format);
        write2bytes(smf, tracks.size());
        write2bytes(smf, timebase);

        // Track chunks
        for (List<Integer> trackData : trackDataArray) {
            smf.addAll(Arrays.asList(0x4d, 0x54, 0x72, 0x6b)); // "MTrk"
            write4bytes(smf, trackData.size());
            smf.addAll(trackData);
        }

        // Convert to byte array
        byte[] result = new byte[smf.size()];
        for (int i = 0; i < smf.size(); i++) {
            result[i] = (byte)(smf.get(i) & 0xFF);
        }
        return result;
    }

    private static void write2bytes(List<Integer> smf, int value) {
        smf.add((value >> 8) & 0xFF);
        smf.add(value & 0xFF);
    }

    private static void write4bytes(List<Integer> smf, int value) {
        smf.add((value >> 24) & 0xFF);
        smf.add((value >> 16) & 0xFF);
        smf.add((value >> 8) & 0xFF);
        smf.add(value & 0xFF);
    }

    private static Token getTokenAt(List<NativeObject> tokens, int index) {
        return fromNativeObject(tokens.get(index), Token.class);
    }

    private static List<Integer> createTrackData(List<NativeObject> tokens, int timebase, int channel) throws MMLParseException {
        List<Integer> trackData = new ArrayList<>();
        int baseLength = timebase;
        int currentTick = 0;
        int restTick = 0;
        int octave = 4;
        int velocity = 100;
        int q = 6;
        int keyShift = 0;
        int p = 0;

        final int OCTAVE_MIN = -1;
        final int OCTAVE_MAX = 10;

        while (p < tokens.size()) {
            Token token = getTokenAt(tokens, p);

            switch (token.command) {
                case "note" -> {
                    int[] abcdefg = {9, 11, 0, 2, 4, 5, 7};
                    int n = "abcdefg".indexOf(token.tone);
                    int note = (octave + 1) * 12 + abcdefg[n] + keyShift;

                    for (char accidental : token.accidentals.toCharArray()) {
                        if (accidental == '+') note++;
                        if (accidental == '-') note--;
                    }

                    if (note < 0 || note > 127) throw new MMLParseException("illegal note number (0-127)");

                    double stepTime = calcNoteLength(token.length, token.dots.length(), timebase);
                    while (p + 1 < tokens.size() && getTokenAt(tokens, p + 1).command.equals("tie")) {
                        p++;
                        stepTime += calcNoteLength(getTokenAt(tokens, p).length, getTokenAt(tokens, p).dots.length(), timebase);
                    }

                    int gateTime = (int)Math.round(stepTime * q / 8.0);

                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0x90 | channel, note, velocity);
                    writeDeltaTick(trackData, gateTime);
                    write(trackData, 0x80 | channel, note, 0);
                    restTick = (int)(stepTime - gateTime);
                    currentTick += stepTime;
                }
                case "rest" -> {
                    double stepTime = calcNoteLength(token.length, token.dots.length(), timebase);
                    restTick += stepTime;
                    currentTick += stepTime;
                }
                case "octave" -> octave = token.number;
                case "octave_up" -> octave++;
                case "octave_down" -> octave--;
                case "note_length" -> baseLength = (int)calcNoteLength(token.length, token.dots.length(), timebase);
                case "gate_time" -> q = token.quantity;
                case "velocity" -> velocity = token.value;
                case "volume" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xb0 | channel, 7, token.value);
                }
                case "pan" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xb0 | channel, 10, token.value + 64);
                }
                case "expression" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xb0 | channel, 11, token.value);
                }
                case "control_change" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xb0 | channel, token.number, token.value);
                }
                case "program_change" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xc0 | channel, token.number);
                }
                case "channel_aftertouch" -> {
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xd0 | channel, token.value);
                }
                case "tempo" -> {
                    int quarterMicroseconds = 60_000_000 / token.value;
                    if (quarterMicroseconds < 1 || quarterMicroseconds > 0xff_ffff) {
                        throw new MMLParseException("illegal tempo");
                    }
                    writeDeltaTick(trackData, restTick);
                    write(trackData, 0xFF, 0x51, 0x03,
                            (quarterMicroseconds >> 16) & 0xFF,
                            (quarterMicroseconds >> 8) & 0xFF,
                            quarterMicroseconds & 0xFF);
                }
                case "key_shift" -> keyShift = token.value;
                case "set_midi_channel" -> channel = token.channel - 1;
            }

            if (octave < OCTAVE_MIN || octave > OCTAVE_MAX) {
                throw new MMLParseException("octave is out of range");
            }

            p++;
        }

        return trackData;
    }

    private static double calcNoteLength(int length, int numDots, int timebase) {
        double noteLength = (length > 0) ? timebase * 4.0 / length : timebase;
        double dottedTime = noteLength;
        for (int i = 0; i < numDots; i++) {
            dottedTime /= 2;
            noteLength += dottedTime;
        }
        return noteLength;
    }

    private static void write(List<Integer> trackData, int...data) {
        for (int b : data) trackData.add(b);
    }

    private static void writeDeltaTick(List<Integer> trackData, int tick) throws MMLParseException {
        if (tick < 0 || tick > 0x0fff_ffff) throw new MMLParseException("illegal length");

        List<Integer> stack = new ArrayList<>();
        do {
            stack.add(tick & 0x7F);
            tick >>>= 7;
        } while (tick > 0);

        for (int i = stack.size() - 1; i >= 0; i--) {
            int b = stack.get(i);
            if (i > 0) b |= 0x80;
            trackData.add(b);
        }
    }
}
