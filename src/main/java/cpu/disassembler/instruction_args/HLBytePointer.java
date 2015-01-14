package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Used when an instruction dereferences the 16-bit register HL as a byte.
 */
public class HLBytePointer implements ValueContainer<Byte> {
    // TODO(ddoucet)
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