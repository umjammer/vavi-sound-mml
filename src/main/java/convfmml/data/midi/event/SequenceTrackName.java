/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class SequenceTrackName extends MetaEvent {

    private final String name;

    public String getName() {
        return name;
    }

    public SequenceTrackName(int deltaTime, String name) {
        super(deltaTime);
        this.name = name;
    }

    @Override
    protected String generateString() {
        return "%d:\tSequence Track Name\t%s".formatted(deltaTime, name);
    }
}
