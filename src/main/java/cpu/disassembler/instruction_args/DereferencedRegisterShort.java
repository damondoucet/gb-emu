package cpu.disassembler.instruction_args;

import cpu.CpuState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Used when an instruction dereferences a 16-bit register as a short.
 */
public class DereferencedRegisterShort implements SettableValueContainer<Short> {
    private final Register16 _r16;

    public DereferencedRegisterShort(Register16 r16) {
        _r16 = r16;
    }

    @Override
    public Short get(CpuState state) {
        short address = _r16.get(state);
        return state.memory.readShort(address);
    }

    @Override
    public void set(CpuState state, Short value) {
        short address = _r16.get(state);
        state.memory.writeShort(address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass().equals(rhs.getClass()) &&
                _r16.equals(((DereferencedRegisterShort)rhs)._r16);
    }

    @Override
    public int hashCode() {
        return _r16.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s)", _r16.toString());
    }
}
