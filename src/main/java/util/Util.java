package util;

import static com.google.common.base.Preconditions.checkArgument;

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

    private static int setBit(int orig, int index, int value) {
        int mask = ~(1 << index);
        int valueMask = value << index;

        return (orig & mask) | valueMask;
    }

    private static int getBit(int value, int index) {
        return (value >> index) & 1;
    }

    public static byte setBit(byte orig, int index, int value) {
        checkArgument(value == 0 || value == 1);
        checkArgument(index >= 0 && index < 8);
        return (byte)setBit((int)orig, index, value);
    }

    public static byte getBit(byte value, int index) {
        checkArgument(index >= 0 && index < 8);
        return (byte)getBit((int) value, index);
    }

    public static short setBit(short orig, int index, int value) {
        checkArgument(value == 0 || value == 1);
        checkArgument(index >= 0 && index < 16);
        return (short)setBit((int) orig, index, value);
    }

    public static short getBit(short value, int index) {
        checkArgument(index >= 0 && index < 16);
        return (short)getBit((int) value, index);
    }

    // The second (0-indexed) least significant nibble. Used for half-carry in
    // 16-bit arithmetic instructions.
    public static short clearTopNibble(short val) {
        return (short)(val & ((1 << 12) - 1));
    }

    public static String byteToHexString(byte b) {
        return String.format("%02X", b).toUpperCase();
    }

    public static String shortToHexString(short s) {
        return String.format("%04X", s).toUpperCase();
    }

    public static short swapBytes(short value) {
        short newLow = shortToHighByte(value);
        short newHigh = (short)(shortToLowByte(value) << 8);
        return (short)(newLow | newHigh);
    }
}
