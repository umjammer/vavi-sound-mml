/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


public class MetaEvent extends Event {

    public MetaEvent(int deltaTime) {
        super(deltaTime);
    }

    @Override
    protected String GenerateString() {
        return "%d:\tOther Meta Event".formatted(deltaTime);
    }
}
