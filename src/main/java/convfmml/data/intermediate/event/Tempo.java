/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.data.intermediate.Position;


public class Tempo implements Cloneable {

    private Position position;
    private int value;

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

    public Tempo(Position position, int value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Tempo clone() {
        return new Tempo(position.clone(), this.value);
    }
}
