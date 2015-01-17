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

    private static boolean add16WouldHalfCarry(short lhs, short rhs) {
        // half-carry for 16-bit is the most recent half-carry
        short lhsHalf = Util.clearTopNibble(lhs);
        short rhsHalf = Util.clearTopNibble(rhs);
        return lhsHalf + rhsHalf >= (1 << 12);
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

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(add16WouldHalfCarry(a, val) ? 1 : 0);

        int carry = (a & 0xFFFF) + (val & 0xFFFF) > 65535 ? 1 : 0;
        state.registerState.flags.setC(carry);
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

    public static class Add8Instruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public Add8Instruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Add8Instruction other = (Add8Instruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("ADD %s", _container.toString());
        }


        @Override
        public void execute(EmulatorState state) {
            add8(state, Register8.A, _container.get(state), false);
        }
    }

    public static class Add16Instruction implements Instruction {
        private final SettableValueContainer<Short> _container;

        public Add16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Add16Instruction other = (Add16Instruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("ADD %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            add16(state, Register16.HL, _container.get(state));
        }
    }

    public static class AdcInstruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public AdcInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            AdcInstruction other = (AdcInstruction)rhs;
            return _container == other._container;
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

    public static class Inc8Instruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public Inc8Instruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Inc8Instruction other = (Inc8Instruction)rhs;
            return _container == other._container;
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
            add8(state, _container, (byte)1, false);
        }
    }

    public static class Inc16Instruction implements Instruction {
        private final SettableValueContainer<Short> _container;

        public Inc16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Inc16Instruction other = (Inc16Instruction)rhs;
            return _container == other._container;
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
            add16(state, _container, (short)1);
        }
    }

    public static class Sub8Instruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public Sub8Instruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Sub8Instruction other = (Sub8Instruction)rhs;
            return _container == other._container;
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

    public static class Sub16Instruction implements Instruction {
        private final SettableValueContainer<Short> _container;

        public Sub16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Sub16Instruction other = (Sub16Instruction)rhs;
            return _container == other._container;
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
            sub16(state, Register16.HL, _container.get(state));
        }
    }

    public static class SbcInstruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public SbcInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SbcInstruction other = (SbcInstruction)rhs;
            return _container == other._container;
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

    public static class Dec8Instruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public Dec8Instruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Dec8Instruction other = (Dec8Instruction)rhs;
            return _container == other._container;
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

    public static class Dec16Instruction implements Instruction {
        private final SettableValueContainer<Short> _container;

        public Dec16Instruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Dec16Instruction other = (Dec16Instruction)rhs;
            return _container == other._container;
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

    public static class ScfInstruction implements Instruction {
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
            return "SCF";
        }

        @Override
        public void execute(EmulatorState state) {
            state.registerState.flags.setC(1);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    public static class CcfInstruction implements Instruction {
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

    public static class CpInstruction implements Instruction {
        private final ValueContainer<Byte> _container;

        public CpInstruction(ValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            CpInstruction other = (CpInstruction)rhs;
            return _container == other._container;
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

    public static class DaaInstruction implements Instruction {
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
            return "DAA";
        }

        @Override
        public void execute(EmulatorState state) {
            throw new NotImplementedException();
        }
    }
}
