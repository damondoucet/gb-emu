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

        private static boolean addWouldHalfCarry(byte lhs, byte rhs) {
            return (((lhs & 0xF) + (rhs & 0xF)) & 0x10) == 0x10;
        }

        @Override
        public void execute(CpuState state) {
            byte oldA = Register8.A.get(state);
            byte oldV = _container.get(state);

            byte newValue = (byte)(oldA + oldV);
            Register8.A.set(state, newValue);

            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(addWouldHalfCarry(oldA, oldV) ? 1 : 0);

            int carry = (oldA & 0xFF) + (oldV & 0xFF) > 255 ? 1 : 0;
            state.registerState.flags.setC(carry);
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
            byte oldA = Register8.A.get(state);
            byte oldV = _container.get(state);

            byte newValue = (byte)(oldA - oldV);
            Register8.A.set(state, newValue);

            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(1);

            // Carries for sub are whether a borrow occurred
            int halfCarry = (oldA & 0xF) < (oldV & 0xF) ? 1 : 0;
            state.registerState.flags.setH(halfCarry);

            int carry = (oldA & 0xff) < (oldV & 0xff) ? 1 : 0;
            state.registerState.flags.setC(carry);
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
}
