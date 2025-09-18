/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class LittleEndianConverter {

    public static byte[] convert(byte[] bs) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            byte[] reversed = Arrays.copyOf(bs, bs.length);
            for (int i = 0, j = bs.length - 1; i < j; i++, j--) {
                byte temp = reversed[i];
                reversed[i] = reversed[j];
                reversed[j] = temp;
            }
            return reversed;
        } else {
            return bs;
        }
    }

    private static byte[] subArray(byte[] src, int startIndex, int count) {
        return Arrays.copyOfRange(src, startIndex, startIndex + count);
    }

    public static short toInt16(byte[] value, int startIndex) {
        byte[] sub = subArray(value, startIndex, Short.BYTES);
        ByteBuffer buffer = ByteBuffer.wrap(convert(sub));
        return buffer.getShort();
    }

    public static int toUInt16(byte[] value, int startIndex) {
        byte[] sub = subArray(value, startIndex, Short.BYTES);
        ByteBuffer buffer = ByteBuffer.wrap(convert(sub));
        return buffer.getShort() & 0xffff;
    }

    public static long toUInt32(byte[] value, int startIndex) {
        byte[] sub = subArray(value, startIndex, Integer.BYTES);
        ByteBuffer buffer = ByteBuffer.wrap(convert(sub));
        return buffer.getInt() & 0xffff_ffffL;
    }
}
