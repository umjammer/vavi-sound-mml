/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class MetaEvent extends Event {

    public MetaEvent(int deltaTime) {
        super(deltaTime);
    }

    @Override
    protected String generateString() {
        return "%d:\tOther Meta Event".formatted(deltaTime);
    }
}
