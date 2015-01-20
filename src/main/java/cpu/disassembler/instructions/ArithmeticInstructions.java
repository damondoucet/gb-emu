package cpu.disassembler.instructions;

import cpu.EmulatorState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instruction_args.SettableValueContainer;
import cpu.disassembler.instruction_args.ValueContainer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Util;

import java.util.Objects;

/**
 * Arithmetic Instructions:
 *      ADD, ADC, SUB, SBC
 *      INC, DEC,
 *      SCF, CCF
 *      CP,
 *      DAA
 */
public final class ArithmeticInstructions {
    private ArithmeticInstructions() {}

    private static boolean add8WouldHalfCarry(byte lhs, byte rhs, int carry) {
        return (((lhs & 0xF) + (rhs & 0xF) + carry) & 0x10) == 0x10;
    }

    // Performs an 8-bit add and sets dest to the resulting value, in addition
    // setting/clearing any flags.
    // We pull these methods out so that ADD, ADC, and INC can share the same
    // code (same for SUB, SBC, and DEC).
    private static void add8(
            EmulatorState state,
            SettableValueContainer<Byte> dest,
            byte val,
            boolean useCarry) {
        byte a = dest.get(state);
        int carry = useCarry && state.registerState.flags.getC() == 1 ? 1 : 0;

        byte newValue = (byte)(a + val + carry);
        dest.set(state, newValue);

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(add8WouldHalfCarry(a, val, carry) ? 1 : 0);

        int newCarry = (a & 0xFF) + (val & 0xFF) + carry > 255 ? 1 : 0;
        state.registerState.flags.setC(newCarry);
    }

    // Add a given value to the value in the destination and store the result
    // in the destination, in addition to setting any necessary flags.
    private static void add16(
            EmulatorState state,
            SettableValueContainer<Short> dest,
            short val) {
        short a = dest.get(state);

        short newValue = (short)(a + val);
        dest.set(state, newValue);

        // Most 16-bit ADD instructions don't affect the Z flag; the ADD SP, r8
        // instruction simply clears it.
        if (dest.equals(Register16.SP))
            state.registerState.flags.setZ(0);

        state.registerState.flags.setN(0);
        state.registerState.flags.setH(Util.add16WouldHalfCarry(a, val) ? 1 : 0);
        state.registerState.flags.setC(Util.add16WouldCarry(a, val) ? 1 : 0);
    }

    // Same as above except for subtraction.
    private static void sub8(
            EmulatorState state,
            SettableValueContainer<Byte> dest,
            byte val,
            boolean useCarry) {
        byte a = dest.get(state);
        int carry = useCarry && state.registerState.flags.getC() == 1 ? 1 : 0;

        byte newValue = (byte)(a - val - carry);
        dest.set(state, newValue);

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(1);

        // Carries for sub are whether a borrow occurred
        int halfCarry = (a & 0xF) < (val & 0xF) + carry ? 1 : 0;
        state.registerState.flags.setH(halfCarry);

        int newCarry = (a & 0xFF) < (val & 0xFF) + carry ? 1 : 0;
        state.registerState.flags.setC(newCarry);
    }

    private static void sub16(
            EmulatorState state,
            SettableValueContainer<Short> dest,
            short val) {
        short a = dest.get(state);

        short newValue = (short)(a - val);
        dest.set(state, newValue);

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(1);

        // Carries for sub are whether a borrow occurred
        state.registerState.flags.setC((a & 0xFFFF) < (val & 0xFFFF) ? 1 : 0);

        // half-carry for 16-bit is the most recent half-carry
        short aHalf = Util.clearTopNibble(a);
        short vHalf = Util.clearTopNibble(val);
        state.registerState.flags.setH(aHalf < vHalf ? 1 : 0);
    }

    public static class Add8Instruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public Add8Instruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Add8Instruction other = (Add8Instruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("ADD A, %s", _container.toString());
        }


        @Override
        public void execute(EmulatorState state) {
            add8(state, Register8.A, _container.get(state), false);
        }
    }

    public static class Add16Instruction extends Instruction {
        private final SettableValueContainer<Short> _dest;
        private final ValueContainer<Short> _src;

        public Add16Instruction(SettableValueContainer<Short> dest, ValueContainer<Short> src) {
            _dest = dest;
            _src = src;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Add16Instruction other = (Add16Instruction)rhs;
            return _dest.equals(other._dest) &&
                    _src.equals(other._src);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_dest, _src);
        }

        @Override
        public String toString() {
            return String.format("ADD %s, %s", _dest.toString(), _src.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            add16(state, _dest, _src.get(state));
        }
    }

    public static class AdcInstruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public AdcInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            AdcInstruction other = (AdcInstruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("ADC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            add8(state, Register8.A, _container.get(state), true);
        }
    }

    public static class Inc8Instruction extends Instruction {
        private final SettableValueContainer<Byte> _container;

        public Inc8Instruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Inc8Instruction other = (Inc8Instruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("INC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            add8(state, _container, (byte) 1, false);
        }
    }

    public static class Inc16Instruction extends Instruction {
        private final SettableValueContainer<Short> _container;

        public Inc16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Inc16Instruction other = (Inc16Instruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("INC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            add16(state, _container, (short) 1);
        }
    }

    public static class SubInstruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public SubInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SubInstruction other = (SubInstruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SUB %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            sub8(state, Register8.A, _container.get(state), false);
        }
    }

    public static class SbcInstruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public SbcInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SbcInstruction other = (SbcInstruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SBC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            sub8(state, Register8.A, _container.get(state), true);
        }
    }

    public static class Dec8Instruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public Dec8Instruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Dec8Instruction other = (Dec8Instruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("DEC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            sub8(state, Register8.A, _container.get(state), false);
        }
    }

    public static class Dec16Instruction extends Instruction {
        private final SettableValueContainer<Short> _container;

        public Dec16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Dec16Instruction other = (Dec16Instruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("DEC %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            sub16(state, _container, (short)1);
        }
    }

    public static class ScfInstruction extends Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass().equals(rhs.getClass());
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public String toString() {
            return "SCF";
        }

        @Override
        public void execute(EmulatorState state) {
            state.registerState.flags.setC(1);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    public static class CcfInstruction extends Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass().equals(rhs.getClass());
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public String toString() {
            return "CCF";
        }

        @Override
        public void execute(EmulatorState state) {
            int newCarry = ~state.registerState.flags.getC() & 1;
            state.registerState.flags.setC(newCarry);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    public static class CpInstruction extends Instruction {
        private final ValueContainer<Byte> _container;

        public CpInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            CpInstruction other = (CpInstruction)rhs;
            return _container.equals(other._container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("CP %s", _container.toString());
        }

        // Compare is the same as doing a subtraction and throwing away the
        // result.
        private static class NoopSettableValueContainer
                implements SettableValueContainer<Byte> {
            private final byte _value;

            public NoopSettableValueContainer(byte value) {
                _value = value;
            }

            @Override
            public Byte get(EmulatorState state) {
                return _value;
            }

            @Override
            public void set(EmulatorState state, Byte value) { }
        }

        @Override
        public void execute(EmulatorState state) {
            sub8(
                    state,
                    new NoopSettableValueContainer(Register8.A.get(state)),
                    _container.get(state),
                    false);
        }
    }

    public static class DaaInstruction extends Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass().equals(rhs.getClass());
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public String toString() {
            return "DAA";
        }

        @Override
        public void execute(EmulatorState state) {
            throw new NotImplementedException();
        }
    }
}
