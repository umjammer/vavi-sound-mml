/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;


import ConvFMML.Data.Intermediate.Position;


public abstract class ChangeEvent {

    protected Position position;
    private int value;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    protected ChangeEvent(Position position, int value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Object clone() {
        try {
            return super.clone(); // TODO
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
