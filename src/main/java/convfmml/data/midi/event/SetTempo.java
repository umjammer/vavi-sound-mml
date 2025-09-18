/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class SetTempo extends MetaEvent {

    public int value;

    public int getValue() {
        return value;
    }

    public SetTempo(int deltaTime, int value) {
        super(deltaTime);
        this.value = value;
    }

    @Override
    protected String generateString() {
        return "%d:\tSet Tempo\t%d".formatted(deltaTime, value);
    }
}
