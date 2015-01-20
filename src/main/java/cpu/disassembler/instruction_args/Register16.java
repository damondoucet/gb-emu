package cpu.disassembler.instruction_args;

import cpu.EmulatorState;

/**
 * Represents a 16-bit register as used by instructions (e.g., which register
 * to add in the ADD instruction).
 */
public class Register16 implements SettableValueContainer<Short> {
    public final static Register16 AF = new Register16(0, "AF");
    public final static Register16 BC = new Register16(1, "BC");
    public final static Register16 DE = new Register16(2, "DE");
    public final static Register16 HL = new Register16(3, "HL");
    public final static Register16 SP = new Register16(4, "SP");
    public final static Register16 PC = new Register16(5, "PC");

    private final int _index;
    private final String _name;

    private Register16(int index, String name) {
        _index = index;
        _name = name;
    }

    @Override
    public Short get(EmulatorState state) {
        return state.registerState.getR16(_index);
    }

    @Override
    public void set(EmulatorState state, Short value) {
        state.registerState.setR16(_index, value);
    }

    @Override
    public int hashCode() {
        return _index;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null &&
                getClass().equals(rhs.getClass()) &&
                _index == ((Register16)rhs)._index;
    }

    @Override
    public String toString() {
        return _name;
    }
}
