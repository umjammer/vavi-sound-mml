/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;


import ConvFMML.Common.Key;
import ConvFMML.Data.Intermediate.Position;


public class KeySignature implements Cloneable {

    private Position position;
    private Key key;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public KeySignature(Position position, Key key) {
        this.position = position;
        this.key = key;
    }

    @Override
    public Object clone() {
        var clone = (KeySignature) super.clone();
        clone.Position = Position.clone();
        return clone;
    }
}
