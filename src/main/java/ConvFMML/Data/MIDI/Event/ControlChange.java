/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


public class ControlChange extends MIDIEvent {

    public int value;

    public int getValue() {
        return value;
    }

    public ControlChange(int deltaTime, int channel, int value) {
        super(deltaTime, channel);
        this.value = value;
    }

    @Override
    protected String GenerateString() {
        return "%d:\tOther Control Change".formatted(deltaTime);
    }
}
