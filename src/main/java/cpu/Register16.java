package cpu;

/**
 * Represents a 16-bit register as used by instructions (e.g., which register
 * to add in the ADD instruction).
 */
public class Register16 {
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

    public short get(RegisterState state) {
        return state.getR16(_index);
    }

    public void set(RegisterState state, short value) {
        state.setR16(_index, value);
    }

    @Override
    public int hashCode() {
        return _index;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs != null &&
                getClass() != rhs.getClass() &&
                _index == ((Register16)rhs)._index;
    }

    @Override
    public String toString() {
        return _name;
    }
}
