/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class SysExEvent extends Event {

    public SysExEvent(int deltaTime) {
        super(deltaTime);
    }

    @Override
    protected String generateString() {
        return "%d:\tSysEx Event".formatted(deltaTime);
    }
}
