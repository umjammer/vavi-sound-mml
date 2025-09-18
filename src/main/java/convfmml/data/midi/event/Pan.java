/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class Pan extends ControlChange {

    public Pan(int deltaTime, int channel, int value) {
        super(deltaTime, channel, value);
    }

    @Override
    protected String generateString() {
        return "%d:\tPan\t%d, %d".formatted(deltaTime, channel, value);
    }
}
