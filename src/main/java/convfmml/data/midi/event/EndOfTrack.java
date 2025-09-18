/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class EndOfTrack extends MetaEvent {

    public EndOfTrack(int deltaTime) {
        super(deltaTime);
    }

    @Override
    protected String generateString() {
        return "%d:\tEnd of Track".formatted(deltaTime);
    }
}
