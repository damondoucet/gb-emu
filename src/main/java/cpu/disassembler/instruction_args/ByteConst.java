package cpu.disassembler.instruction_args;

import cpu.EmulatorState;
import util.Util;

/**
 * Represents a constant byte value for use as an instruction argument.
 */
public class ByteConst implements ValueContainer<Byte> {
    private final byte _value;

    public ByteConst(byte value) {
        _value = value;
    }

    @Override
    public Byte get(EmulatorState state) {
        return _value;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(_value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass().equals(rhs.getClass()) &&
                _value == ((ByteConst)rhs)._value;
    }

    @Override
    public String toString() {
        return Util.byteToHexString(_value);
    }
}
