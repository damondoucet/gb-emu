package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 8-bit pointer for use as an instruction argument.
 */
public class BytePointer implements ValueContainer<Byte> {
    // Memory addresses in the Gameboy are 16 bits long.
    private final short _address;

    public BytePointer(short address) {
        _address = address;
    }

    // TODO(ddoucet): get+set
    @Override
    public Byte get(CpuState state) {
        throw new NotImplementedException();
    }

    @Override
    public void set(CpuState state, Byte value) {
        throw new NotImplementedException();
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
        return Integer.toHexString(_address).toUpperCase();
    }
}
