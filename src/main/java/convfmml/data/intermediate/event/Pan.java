/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public class Pan extends ChangeEvent implements Cloneable {

    public Pan(Position position, int value) {
        super(position, value);
    }

    @Override
    public Pan clone() {
        return new Pan(position.clone(), value);
    }
}
