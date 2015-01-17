package cpu.disassembler.instruction_args;

import cpu.EmulatorState;

/**
 * Used when an instruction dereferences a 16-bit register as a byte.
 */
public class DereferencedRegisterByte implements SettableValueContainer<Byte> {
    private final Register16 _r16;

    public DereferencedRegisterByte(Register16 r16) {
        _r16 = r16;
    }

    @Override
    public Byte get(EmulatorState state) {
        short address = _r16.get(state);
        return state.memory.readByte(address);
    }

    @Override
    public void set(EmulatorState state, Byte value) {
        short address = _r16.get(state);
        state.memory.writeByte(address, value);
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null && getClass().equals(rhs.getClass()) &&
                _r16.equals(((DereferencedRegisterByte)rhs)._r16);
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
