package cpu.disassembler.instruction_args;

import cpu.EmulatorState;

/**
 * Represents an 8-bit register, as used by instructions (e.g., which register
 * to add in the ADD instruction).
 */
public class Register8 implements SettableValueContainer<Byte> {
    // For reasoning on these indices, look at the internals of the
    // RegisterState class.
    public final static Register8 A = new Register8(0, "A");
    public final static Register8 B = new Register8(2, "B");
    public final static Register8 C = new Register8(3, "C");
    public final static Register8 D = new Register8(4, "D");
    public final static Register8 E = new Register8(5, "E");
    public final static Register8 H = new Register8(6, "H");
    public final static Register8 L = new Register8(7, "L");

    private final int _index;
    private final String _name;

    private Register8(int index, String name) {
        _index = index;
        _name = name;
    }

    @Override
    public Byte get(EmulatorState state) {
        return state.registerState.getR8(_index);
    }

    @Override
    public void set(EmulatorState state, Byte value) {
        state.registerState.setR8(_index, value);
    }

    @Override
    public int hashCode() {
        return _index;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null &&
                getClass().equals(rhs.getClass()) &&
                _index == ((Register8)rhs)._index;
    }

    @Override
    public String toString() {
        return _name;
    }
}
