package cpu;

import util.Util;

/**
 * Holds the register state of the CPU.
 *
 * These methods shouldn't be called directly by callers; instead, use, e.g.,
 * Register8.A.get(registerState) or Register8.A.set(registerState, newValue)
 *
 * The usable registers for the Gameboy CPU are as follows:
 *
 *     16-bit     Hi8     Lo8
 *       AF        A       -
 *       BC        B       C
 *       DE        D       E
 *       HL        H       L
 *       SP        -       -
 *       PC        -       -
 *
 * All values in this class are stored in terms of the 8-bit components (even
 * the ones that aren't public-facing). Thus the register AF (16-bit index 0)
 * is composed of A (8-bit index 0) and another register with 8-bit index 1.
 * Similarly, HL (16-bit index 3) is composed of H (8-bit index 6) and L (8-bit
 * index 7).
 */
public class RegisterState {
    private final static int MAX_8BIT_REGS = 12;

    private final byte[] _r8Values;
    public final Flags flags;

    public RegisterState() {
        _r8Values = new byte[MAX_8BIT_REGS];
        flags = new Flags(this);
    }

    public byte getR8(int r8index) {
        return _r8Values[r8index];
    }

    public void setR8(int r8index, byte value) {
        _r8Values[r8index] = value;
    }

    public short getR16(int r16index) {
        byte high = _r8Values[r16index * 2];
        byte low = _r8Values[r16index * 2 + 1];
        return Util.shortFromBytes(high, low);
    }

    public void setR16(int r16index, short value) {
        _r8Values[r16index * 2] = Util.shortToHighByte(value);
        _r8Values[r16index * 2 + 1] = Util.shortToLowByte(value);
    }
}
