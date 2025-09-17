/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;

import ConvFMML.Data.Intermediate.Position;


public abstract class NoteRest implements Cloneable {

    protected Position start;
    protected Position end;
    private boolean tieFlag = false;

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getEnd() {
        return end;
    }

    public void setEnd(Position end) {
        this.end = end;
    }

    public boolean isTieFlag() {
        return tieFlag;
    }

    public void setTieFlag(boolean tieFlag) {
        this.tieFlag = tieFlag;
    }

    protected NoteRest(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public abstract NoteRest clone();
}
