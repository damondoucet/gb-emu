package util;

import cpu.disassembler.InstructionDecoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Some misc. utility functions.
 */
public final class Util {
    private Util() {}

    public static byte[] bytesFromFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

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

    // The second (0-indexed) least significant nibble. Used for half-carry in
    // 16-bit arithmetic instructions.
    public static short clearTopNibble(short val) {
        return (short)(val & ((1 << 12) - 1));
    }

    public static String byteToHexStringWithoutPrefix(byte b) {
        return String.format("%02X", b).toUpperCase();
    }

    public static String byteToHexString(byte b) {
        return "$" + byteToHexStringWithoutPrefix(b);
    }

    public static String shortToHexString(short s) {
        return "$" + String.format("%04X", s).toUpperCase();
    }

    public static short swapBytes(short value) {
        short newLow = shortToHighByte(value);
        short newHigh = (short)(shortToLowByte(value) << 8);
        return (short)(newLow | newHigh);
    }

    public static boolean add16WouldCarry(short lhs, short rhs) {
        return (lhs & 0xFFFF) + (rhs & 0xFFFF) > 65535;
    }

    public static boolean add16WouldHalfCarry(short lhs, short rhs) {
        // half-carry for 16-bit is the most recent half-carry
        short lhsHalf = Util.clearTopNibble(lhs);
        short rhsHalf = Util.clearTopNibble(rhs);
        return lhsHalf + rhsHalf >= (1 << 12);
    }
}
