package cpu.disassembler.instructions;

import cpu.CpuState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instruction_args.SettableValueContainer;
import cpu.disassembler.instruction_args.ValueContainer;
import util.Util;

import java.util.Objects;

/**
 * Arithmetic Instructions:
 *      ADD, ADC, SUB, SBC
 *      INC, DEC,
 *      SCF, CCF
 *      CP, DAA
 */
public final class ArithmeticInstructions {
    private ArithmeticInstructions() {}

    private static boolean add8WouldHalfCarry(byte lhs, byte rhs, int carry) {
        return (((lhs & 0xF) + (rhs & 0xF) + carry) & 0x10) == 0x10;
    }

    // Performs an 8-bit add and sets the A register to the resulting value,
    // in addition to setting/clearing any flags.
    // We pull these methods out so that ADD and ADC can share the same code
    // (same for SUB and SBC).
    private static void add8(CpuState state, ValueContainer<Byte> container, boolean useCarry) {
        byte a = Register8.A.get(state);
        byte b = container.get(state);

        int carry = useCarry && state.registerState.flags.getC() == 1 ? 1 : 0;
        byte newValue = (byte)(a + b + carry);
        Register8.A.set(state, newValue);

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(add8WouldHalfCarry(a, b, carry) ? 1 : 0);

        int newCarry = (a & 0xFF) + (b & 0xFF) + carry > 255 ? 1 : 0;
        state.registerState.flags.setC(newCarry);
    }

    // Same as above except for subtraction.
    private static void sub8(CpuState state, ValueContainer<Byte> container, boolean useCarry) {
        byte a = Register8.A.get(state);
        byte b = container.get(state);
        int carry = useCarry && state.registerState.flags.getC() == 1 ? 1 : 0;

        byte newValue = (byte)(a - b - carry);
        Register8.A.set(state, newValue);

        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(1);

        // Carries for sub are whether a borrow occurred

        // TODO(ddoucet): Is this the correct order of operations for SBC?
        // i.e. does it subtract b and then subtract carry, or does it subtract
        // the sum b + carry? This affects whether the half carry bit should be
        // set. Consider the case a = 0x10, b = 0x0f, carry = 1 -> summing them
        // would give 0x10 - 0x10, no half carry; not summing them would give
        // (0x10 - 0x0f) - 1 = 1 - 1 = 0, but the half-carry would have been
        // triggered by the 0x10 - 0x0f, right?
        // Currently, the code assumes that it subtracts the sum b+carry.

        int halfCarry = (a & 0xF) < ((b + carry) & 0xF) ? 1 : 0;
        state.registerState.flags.setH(halfCarry);

        int newCarry = (a & 0xFF) < ((b + carry) & 0xFF) ? 1 : 0;
        state.registerState.flags.setC(newCarry);
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
        public void execute(CpuState state) {
            add8(state, _container, false);
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
        public void execute(CpuState state) {
            short oldHL = Register16.HL.get(state);
            short oldV = _container.get(state);

            short newValue = (short)(oldHL + oldV);
            Register16.HL.set(state, newValue);

            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(0);

            int carry = (oldHL & 0xFFFF) + (oldV & 0xFFFF) > 65535 ? 1 : 0;
            state.registerState.flags.setC(carry);

            // half-carry for 16-bit is the most recent half-carry
            short hlHalf = Util.clearTopNibble(oldHL);
            short vHalf = Util.clearTopNibble(oldV);
            int halfCarry = hlHalf + vHalf >= (1 << 12) ? 1 : 0;
            state.registerState.flags.setH(halfCarry);
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
        public void execute(CpuState state) {
            add8(state, _container, true);
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
        public void execute(CpuState state) {
            sub8(state, _container, false);
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
        public void execute(CpuState state) {
            short oldHL = Register16.HL.get(state);
            short oldV = _container.get(state);

            short newValue = (short)(oldHL - oldV);
            Register16.HL.set(state, newValue);

            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(1);

            // Carries for sub are whether a borrow occurred
            state.registerState.flags.setC((oldHL & 0xFFFF) < (oldV & 0xFFFF) ? 1 : 0);

            // half-carry for 16-bit is the most recent half-carry
            short hlHalf = Util.clearTopNibble(oldHL);
            short vHalf = Util.clearTopNibble(oldV);
            state.registerState.flags.setH(hlHalf < vHalf ? 1 : 0);
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
        public void execute(CpuState state) {
            sub8(state, _container, true);
        }
    }
}
