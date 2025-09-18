/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public abstract class ChangeEvent {

    protected Position position;
    protected int value;

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
}
