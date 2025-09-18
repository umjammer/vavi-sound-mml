/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate.event;

import convfmml.Common.Key;
import convfmml.data.intermediate.Position;


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
    public KeySignature clone() {
        return new KeySignature(position.clone(), this.key);
    }
}
