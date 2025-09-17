/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;

import ConvFMML.Data.Intermediate.Position;


public class Volume extends ChangeEvent implements Cloneable {

    public Volume(Position position, int value) {
        super(position, value);
    }

    @Override
    public ChangeEvent clone() {
        var clone = (Volume) MemberwiseClone();
        clone.position = position.clone();
        return clone;
    }
}
