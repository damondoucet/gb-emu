package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Util;

/**
 * 16-bit pointer for use as an instruction argument.
 */
public class ShortPointer implements SettableValueContainer<Short> {
    // Memory addresses in the Gameboy are 16 bits long.
    private final short _address;

    public ShortPointer(short address) {
        _address = address;
    }

    @Override
    public Short get(CpuState state) {
        return state.memory.readShort(_address);
    }

    @Override
    public void set(CpuState state, Short value) {
        state.memory.writeShort(_address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass() == rhs.getClass() &&
                _address == ((ShortPointer)rhs)._address;
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
