package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
    public Byte get(CpuState state) {
        return state.memory.readByte(_address);
    }

    @Override
    public void set(CpuState state, Byte value) {
        state.memory.writeByte(_address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass() == rhs.getClass() &&
                _address == ((BytePointer)rhs)._address;
    }
    @Override
    public int hashCode() {
        return _address;
    }

    @Override
    public String toString() {
        return Util.shortToHexString(_address);
    }
}
