/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public class Rest extends NoteRest {

    public Rest(Position start, Position end) {
        super(start, end);
    }

    @Override
    public NoteRest clone() {
        return new Rest(start.clone(), end.clone());
    }
}
