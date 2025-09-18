/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public class Volume extends ChangeEvent implements Cloneable {

    public Volume(Position position, int value) {
        super(position, value);
    }

    @Override
    public Volume clone() {
        return new Volume(position.clone(), value);
    }
}
