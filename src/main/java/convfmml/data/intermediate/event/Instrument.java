/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public class Instrument extends ChangeEvent implements Cloneable {

    public Instrument(Position position, int value) {
        super(position, value);
    }

    @Override
    public Instrument clone() {
        return new Instrument(position.clone(), value);
    }
}
