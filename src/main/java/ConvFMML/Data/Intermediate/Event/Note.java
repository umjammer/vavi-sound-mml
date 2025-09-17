/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;

import ConvFMML.Data.Intermediate.Position;


public class Note extends NoteRest {

    private int keyNumber;
    private int velocity;

    public int getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(int keyNumber) {
        this.keyNumber = keyNumber;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Note(Position start, Position end, int keyNumber, int velocity) {
        this(start, end);
        this.keyNumber = keyNumber;
        this.velocity = velocity;
    }

    private Note(Position start, Position end) {
        super(start, end);
    }

    @Override
    public Note clone() {
        var clone = new Note(start, end);
        return clone;
    }
}
