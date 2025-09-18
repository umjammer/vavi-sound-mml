/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public abstract class Note extends MIDIEvent {

    protected int number;

    public int getNumber() {
        return number;
    }

    protected int velocity;

    public int getVelocity() {
        return velocity;
    }

    protected Note(int deltaTime, int channel, int number, int velocity) {
        super(deltaTime, channel);
        this.number = number;
        this.velocity = velocity;
    }

    @Override
    protected abstract String generateString();
}
