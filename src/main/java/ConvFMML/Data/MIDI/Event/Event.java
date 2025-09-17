/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


public abstract class Event {

    protected final int deltaTime;

    public int getDeltaTime() {
        return deltaTime;
    }

    protected Event(int deltaTime) {
        this.deltaTime = deltaTime;
    }

    @Override
    public String toString() {
        return GenerateString();
    }

    protected abstract String GenerateString();
}
