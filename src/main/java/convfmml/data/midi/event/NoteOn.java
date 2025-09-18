/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class NoteOn extends Note {

    public NoteOn(int deltaTime, int channel, int number, int velocity) {
        super(deltaTime, channel, number, velocity);
    }

    @Override
    protected String generateString() {
        return "%d:\tNote On\t%d, %d, %d".formatted(deltaTime, channel, number, velocity);
    }
}
