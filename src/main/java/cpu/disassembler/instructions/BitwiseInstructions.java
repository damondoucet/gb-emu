package cpu.disassembler.instructions;

import cpu.CpuState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instruction_args.SettableValueContainer;
import util.Util;

import java.util.Objects;

/**
 * Instructions that perform bitwise operations:
 *      RLA, RRA, RLCA, RRCA,
 *      RL, RR, RLC, RRC,
 *      SLA, SRA, SRL,
 *      SWAP, BIT, RES, SET,
 *      AND, OR, XOR, CPL
 *
 *  TODO(ddoucet): AND/OR/XOR/CPL
 */
public final class BitwiseInstructions {
    private BitwiseInstructions() {}

    // The RL, RR, RLC, and RRC instructions all provide a different
    // instruction that acts on the A register in less cycles, so we
    // create a separate instruction but de-duplicate the execution
    // here.
    private static void executeRlc(CpuState state, SettableValueContainer<Byte> container) {
        byte oldValue = container.get(state);
        int newCarry = (oldValue & 0xFF) >>> 7;
        byte newValue = (byte)((oldValue << 1) | newCarry);

        container.set(state, newValue);
        state.registerState.flags.setC(newCarry);
        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(0);
    }

    private static void executeRrc(CpuState state, SettableValueContainer<Byte> container) {
        byte oldValue = container.get(state);
        int newCarry = oldValue & 1;
        byte newValue = (byte)(((oldValue & 0xFF) >>> 1) | (newCarry << 7));

        container.set(state, newValue);
        state.registerState.flags.setC(newCarry);
        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(0);
    }

    private static void executeRl(CpuState state, SettableValueContainer<Byte> container) {
        int oldCarry = state.registerState.flags.getC();
        byte oldValue = container.get(state);
        byte newValue = (byte)((oldValue << 1) | oldCarry);
        int newCarry = (oldValue & 0xFF) >>> 7;

        container.set(state, newValue);
        state.registerState.flags.setC(newCarry);
        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(0);
    }

    private static void executeRr(CpuState state, SettableValueContainer<Byte> container) {
        int shiftedCarry = state.registerState.flags.getC() << 7;
        byte oldValue = container.get(state);
        byte newValue = (byte)(((oldValue & 0xFF) >>> 1) | shiftedCarry);
        int carry = oldValue & 1;

        container.set(state, newValue);
        state.registerState.flags.setC(carry);
        state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        state.registerState.flags.setN(0);
        state.registerState.flags.setH(0);
    }

    public static class RlaInstruction implements Instruction {
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
            return "RLA";
        }

        @Override
        public void execute(CpuState state) {
            executeRl(state, Register8.A);
        }
    }

    public static class RraInstruction implements Instruction {
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
            return "RRA";
        }

        @Override
        public void execute(CpuState state) {
            executeRr(state, Register8.A);
        }
    }

    public static class RrcaInstruction implements Instruction {
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
            return "RRCA";
        }

        @Override
        public void execute(CpuState state) {
            executeRrc(state, Register8.A);
        }
    }

    public static class RlcaInstruction implements Instruction {
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
            return "RLCA";
        }

        @Override
        public void execute(CpuState state) {
            executeRlc(state, Register8.A);
        }
    }

    // Performs a rotate-left on an 8-bit value container and sets the carry
    // flag to be the original 7th bit.
    public static class RlcInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public RlcInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RlcInstruction other = (RlcInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("RLC %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            executeRlc(state, _container);
        }
    }

    // Performs a rotate-right on an 8-bit value container and sets the carry
    // flag to be the original 0th bit.
    public static class RrcInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public RrcInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RrcInstruction other = (RrcInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("RRC %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            executeRrc(state, _container);
        }
    }

    // Performs a rotate-left through carry on an 8-bit value container.
    public static class RlInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public RlInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RlInstruction other = (RlInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("RL %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            executeRl(state, _container);
        }
    }

    // Performs a rotate-right through carry on an 8-bit value container.
    public static class RrInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public RrInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RrInstruction other = (RrInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("RR %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            executeRr(state, _container);
        }
    }

    // Performs an arithmetic shift left by one on an 8-bit value container and
    // stores the lost bit in the carry flag.
    public static class SlaInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public SlaInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SlaInstruction other = (SlaInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SLA %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte oldValue = _container.get(state);
            byte newValue = (byte)(oldValue << 1);
            int carry = (byte)((oldValue & 0xFF) >>> 7);

            _container.set(state, newValue);
            state.registerState.flags.setC(carry);
            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    // Performs an arithmetic shift right by one on an 8-bit value container
    // and stores the lost bit in the carry flag.
    public static class SraInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public SraInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SraInstruction other = (SraInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SRA %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte oldValue = _container.get(state);
            byte newValue = (byte)(oldValue >> 1);
            int carry = oldValue & 1;

            _container.set(state, newValue);
            state.registerState.flags.setC(carry);
            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    // Performs a logical shift right by one on an 8-bit value container and
    // stores the lost bit in the carry flag.
    public static class SrlInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public SrlInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SrlInstruction other = (SrlInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SRL %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte oldValue = _container.get(state);
            byte newValue = (byte)((oldValue & 0xFF) >>> 1);
            int carry = oldValue & 1;

            _container.set(state, newValue);
            state.registerState.flags.setC(carry);
            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(0);
        }
    }

    // Swaps the low and high nibbles of an 8-bit value container.
    public static class SwapInstruction implements Instruction {
        private final SettableValueContainer<Byte> _container;

        public SwapInstruction(SettableValueContainer<Byte> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SwapInstruction other = (SwapInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("SWAP %s", _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte value = _container.get(state);
            byte lowNibble = (byte)(value & 0xF);
            byte highNibble = (byte)(value >> 4);
            byte newValue = (byte)((lowNibble << 4) | highNibble);

            _container.set(state, newValue);
            state.registerState.flags.setZ(newValue == 0 ? 1 : 0);
        }
    }

    // Tests a bit in a given 8-bit value container. Stores the result in the Z
    // flag.
    public static class BitInstruction implements Instruction {
        private final int _bitIndex;
        private final SettableValueContainer<Byte> _container;

        public BitInstruction(int bitIndex, SettableValueContainer<Byte> container) {
            _bitIndex = bitIndex;
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            BitInstruction other = (BitInstruction)rhs;
            return _bitIndex == other._bitIndex &&
                    _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_bitIndex, _container);
        }

        @Override
        public String toString() {
            return String.format("BIT %d, %s",
                    _bitIndex,
                    _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            int bit = Util.getBit(_container.get(state), _bitIndex);
            int isZero = ~bit & 1;

            state.registerState.flags.setZ(isZero);
            state.registerState.flags.setH(1);
            state.registerState.flags.setN(0);
        }
    }

    // Resets a bit in a given 8-bit value container.
    public static class ResInstruction implements Instruction {
        private final int _bitIndex;
        private final SettableValueContainer<Byte> _container;

        public ResInstruction(int bitIndex, SettableValueContainer<Byte> container) {
            _bitIndex = bitIndex;
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            ResInstruction other = (ResInstruction)rhs;
            return _bitIndex == other._bitIndex &&
                    _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_bitIndex, _container);
        }

        @Override
        public String toString() {
            return String.format("RES %d, %s",
                    _bitIndex,
                    _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte newValue = Util.setBit(_container.get(state), _bitIndex, 0);
            _container.set(state, newValue);
        }
    }

    // Sets a bit in a given 8-bit value container.
    public static class SetInstruction implements Instruction {
        private final int _bitIndex;
        private final SettableValueContainer<Byte> _container;

        public SetInstruction(int bitIndex, SettableValueContainer<Byte> container) {
            _bitIndex = bitIndex;
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            SetInstruction other = (SetInstruction)rhs;
            return _bitIndex == other._bitIndex &&
                    _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_bitIndex, _container);
        }

        @Override
        public String toString() {
            return String.format("SET %d, %s", _bitIndex, _container.toString());
        }

        @Override
        public void execute(CpuState state) {
            byte newValue = Util.setBit(_container.get(state), _bitIndex, 1);
            _container.set(state, newValue);
        }
    }
}