/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.converter;


public class LengthElement implements Cloneable {

    private final int length;
    private final int gate;
    private final boolean tripletFlag;


    public int getLength() {
        return length;
    }

    public int getGate() {
        return gate;
    }

    public boolean isTripletFlag() {
        return tripletFlag;
    }

    public LengthElement(int length, int gate, boolean tripletFlag) {
        this.length = length;
        this.gate = gate;
        this.tripletFlag = tripletFlag;
    }

    @Override
    public LengthElement clone() {
        try {
            return (LengthElement) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}

