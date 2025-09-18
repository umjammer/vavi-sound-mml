/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class ProgramChange extends MIDIEvent {

    private final int number;

    public int getNumber() {
        return number;
    }

    public ProgramChange(int deltaTime, int channel, int number) {
        super(deltaTime, channel);
        this.number = number;
    }

    @Override
    protected String generateString() {
        return "%d:\tProgram Change\t%d, %d".formatted(deltaTime, channel, number);
    }
}
