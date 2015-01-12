package cpu;

import util.Util;

/**
 * Manages the four flags in the hidden F register.
 */
public class Flags {
    // Which 8-bit register the flags register is
    private final static int FLAGS_REG = 1;

    // Which bit index each flag is in the 8-bit flags register
    private final static int Z_INDEX = 7;
    private final static int N_INDEX = 6;
    private final static int H_INDEX = 5;
    private final static int C_INDEX = 4;

    private final RegisterState _state;

    public Flags(RegisterState state) {
        _state = state;
    }

    private byte getFlags() {
        return _state.getR8(FLAGS_REG);
    }

    private void setFlag(int bitIndex, int value) {
        byte newFlags = Util.setBit(getFlags(), bitIndex, value);
        _state.setR8(FLAGS_REG, newFlags);
    }

    private int getFlag(int bitIndex) {
        return Util.getBit(getFlags(), bitIndex);
    }

    public void setZ(int value) {
        setFlag(Z_INDEX, value);
    }

    public int getZ() {
        return getFlag(Z_INDEX);
    }

    public void setC(int value) {
        setFlag(C_INDEX, value);
    }

    public int getC() {
        return getFlag(C_INDEX);
    }

    public void setN(int value) {
        setFlag(N_INDEX, value);
    }

    public int getN() {
        return getFlag(N_INDEX);
    }

    public void setH(int value) {
        setFlag(H_INDEX, value);
    }

    public int getH() {
        return getFlag(H_INDEX);
    }
}
