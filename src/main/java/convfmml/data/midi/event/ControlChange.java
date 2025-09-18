/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


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
    protected String generateString() {
        return "%d:\tOther Control Change".formatted(deltaTime);
    }
}
