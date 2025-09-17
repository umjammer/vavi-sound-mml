/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;


public class LittleEndianConverter {

    public static byte[] Convert(byte[] bs) {
        if (BitConverter.IsLittleEndian == true) {
            return bs.Reverse().ToArray();
        } else {
            return bs;
        }
    }

    private static byte[] SubArray(byte[] src, int startIndex, int count) {
        byte[] ret = new byte[count];
        System.arraycopy(src, startIndex, ret, 0, count);
        return ret;
    }

    public static short ToInt16(byte[] value, int startIndex) {
        byte[] sub = SubArray(value, startIndex, Short.BYTES));
        return BitConverter.ToInt16(Convert(sub), 0);
    }

    public static ushort ToUInt16(byte[] value, int startIndex) {
        byte[] sub = SubArray(value, startIndex, Short.BYTES));
        return BitConverter.ToUInt16(Convert(sub), 0);
    }

    public static int ToUInt32(byte[] value, int startIndex) {
        byte[] sub = SubArray(value, startIndex, Integer.BYTES));
        return BitConverter.ToUInt32(Convert(sub), 0);
    }
}
