/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;


import ConvFMML.Data.Intermediate.Position;


public class Pan extends ChangeEvent implements Cloneable {

    public Pan(Position position, int value) {
        super(position, value);
    }

    @Override
    public Object clone() {
        var clone = (Pan) super.clone();
        clone.position = Position.clone();
        return clone;
    }
}
