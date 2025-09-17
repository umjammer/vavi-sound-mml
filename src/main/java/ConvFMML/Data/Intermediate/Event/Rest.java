/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;


import ConvFMML.Data.Intermediate.Position;


public class Rest extends NoteRest {

    public Rest(Position start, Position end) {
        super(start, end);
    }

    @Override
    public Object clone() {
        return new Rest(Start.clone(), End.clone());
    }
}
