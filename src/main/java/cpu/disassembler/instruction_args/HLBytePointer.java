package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Used when an instruction dereferences the 16-bit register HL as a byte.
 */
public class HLBytePointer implements SettableValueContainer<Byte> {
    @Override
    public Byte get(CpuState state) {
        short address = Register16.HL.get(state);
        return state.memory.readByte(address);
    }

    @Override
    public void set(CpuState state, Byte value) {
        short address = Register16.HL.get(state);
        state.memory.writeByte(address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass() == rhs.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "(HL)";
    }
}
