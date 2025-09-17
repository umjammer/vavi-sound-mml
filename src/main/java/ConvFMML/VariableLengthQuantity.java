/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;

public class VariableLengthQuantity {

    private final long value;
    private final int bytesLength;

    public VariableLengthQuantity(byte[] bs, int startIndex) {
        byte b;
        int index = startIndex;
        long tempValue = 0;

        while (((b = bs[index++]) & 0x80) != 0) {
            tempValue |= (b & 0x7f);
            tempValue <<= 7;
        }
        tempValue |= (b & 0xFF); // Use 0xFF to treat byte as unsigned

        this.value = tempValue;
        this.bytesLength = index - startIndex;
    }

    public long getValue() {
        return value;
    }

    public int getBytesLength() {
        return bytesLength;
    }
}
