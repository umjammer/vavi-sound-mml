/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;

import ConvFMML.Data.Intermediate.Position;


public class Instrument extends ChangeEvent implements Cloneable {

    public Instrument(Position position, int value) {
        super(position, value);
    }

    @Override
    public Object clone() {
        var clone = (Instrument) super.clone();
        clone.position = Position.clone();
        return clone;
    }
}
