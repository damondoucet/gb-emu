package cpu.disassembler.instruction_args;

import cpu.EmulatorState;
import util.Util;

/**
 * Represents a constant short value for use as an instruction argument.
 */
public class ShortConst implements ValueContainer<Short> {
    private final short _value;

    public ShortConst(short value) {
        _value = value;
    }

    @Override
    public Short get(EmulatorState state) {
        return _value;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(_value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass().equals(rhs.getClass()) &&
                _value == ((ShortConst)rhs)._value;
    }

    @Override
    public String toString() {
        return Util.shortToHexString(_value);
    }
}
