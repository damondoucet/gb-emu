package cpu.disassembler.instruction_args;

import cpu.EmulatorState;
import util.Util;

/**
 * 8-bit pointer for use as an instruction argument.
 */
public class BytePointer implements SettableValueContainer<Byte> {
    // Memory addresses in the Gameboy are 16 bits long.
    private final short _address;

    public BytePointer(short address) {
        _address = address;
    }

    @Override
    public Byte get(EmulatorState state) {
        return state.memory.readByte(_address);
    }

    @Override
    public void set(EmulatorState state, Byte value) {
        state.memory.writeByte(_address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass().equals(rhs.getClass()) &&
                _address == ((BytePointer)rhs)._address;
    }
    @Override
    public int hashCode() {
        return _address;
    }

    @Override
    public String toString() {
        return String.format("(%s)", Util.shortToHexString(_address));
    }
}
