package util;

/**
 * Some misc. utility functions.
 */
public final class Util {
    private Util() {}

    public static short shortFromBytes(byte high, byte low) {
        // if Java had unsigned bytes, we could do
        // return ((high << 8) | low), but alas...
        int highInt = high & 0xFF;
        int lowInt = low & 0xFF;
        return (short)((highInt << 8) | lowInt);
    }

    public static byte shortToHighByte(short value) {
        return (byte)(value >> 8);
    }

    public static byte shortToLowByte(short value) {
        return (byte)value;
    }
}
