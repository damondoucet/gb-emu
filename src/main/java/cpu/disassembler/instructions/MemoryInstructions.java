package cpu.disassembler.instructions;

import cpu.EmulatorState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.*;
import util.Util;

import java.util.Objects;

/**
 * Instructions dealing with memory:
 *      LD, LDH, PUSH, POP
 */
public final class MemoryInstructions {
    private MemoryInstructions() {}

    private static void updateSp(EmulatorState state, int delta) {
        short newValue = (short)(Register16.SP.get(state) + delta);
        Register16.SP.set(state, newValue);
    }

    public static void push(EmulatorState state, short value) {
        updateSp(state, -2);

        // When pushing, the most significant byte goes first on the stack.
        short sp = Register16.SP.get(state);
        state.memory.writeShort(sp, Util.swapBytes(value));
    }

    public static short pop(EmulatorState state) {
        short sp = Register16.SP.get(state);
        short returnValue = Util.swapBytes(state.memory.readShort(sp));

        updateSp(state, 2);
        return returnValue;
    }

    private static void load8(
            EmulatorState state,
            SettableValueContainer<Byte> dest,
            ValueContainer<Byte> src) {
        dest.set(state, src.get(state));
    }

    private static void ldAHL(EmulatorState state, boolean writeToHL) {
        SettableValueContainer<Byte> bytePtr = new DereferencedRegisterByte(
                Register16.HL);

        if (writeToHL)
            load8(state, bytePtr, Register8.A);
        else
            load8(state, Register8.A, bytePtr);

    }

    public static class PushInstruction extends Instruction {
        private final ValueContainer<Short> _container;

        public PushInstruction(ValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            PushInstruction other = (PushInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("PUSH %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            push(state, _container.get(state));
        }
    }

    public static class PopInstruction extends Instruction {
        private final SettableValueContainer<Short> _container;

        public PopInstruction(SettableValueContainer<Short> container) {
            _container = container;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            PopInstruction other = (PopInstruction)rhs;
            return _container == other._container;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_container);
        }

        @Override
        public String toString() {
            return String.format("POP %s", _container.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            _container.set(state, pop(state));
        }
    }

    public static class Ld8Instruction extends Instruction {
        private final SettableValueContainer<Byte> _dest;
        private final ValueContainer<Byte> _src;

        public Ld8Instruction(SettableValueContainer<Byte> dest, ValueContainer<Byte> src) {
            _dest = dest;
            _src = src;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Ld8Instruction other = (Ld8Instruction)rhs;
            return _dest == other._dest &&
                    _src == other._src;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_dest, _src);
        }

        @Override
        public String toString() {
            return String.format("LD %s, %s", _dest.toString(), _src.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            load8(state, _dest, _src);
        }
    }

    public static class Ld16Instruction extends Instruction {
        private final SettableValueContainer<Short> _dest;
        private final ValueContainer<Short> _src;

        public Ld16Instruction(SettableValueContainer<Short> dest, ValueContainer<Short> src) {
            _dest = dest;
            _src = src;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            Ld16Instruction other = (Ld16Instruction)rhs;
            return _dest == other._dest &&
                    _src == other._src;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_dest, _src);
        }

        @Override
        public String toString() {
            return String.format("LD %s, %s", _dest.toString(), _src.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            _dest.set(state, _src.get(state));
        }
    }

    // Load A to/from HL and increment HL.
    // _writeToHL = true -> LDI (HL), A
    // _writeToHL = false -> LDI A, (HL)
    public static class LdiInstruction extends Instruction {
        private final boolean _writeToHL;

        private LdiInstruction(boolean writeToHL) {
            _writeToHL = writeToHL;
        }

        public static LdiInstruction ldiFromHL() {
            return new LdiInstruction(false);
        }

        public static LdiInstruction ldiToHL() {
            return new LdiInstruction(true);
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            LdiInstruction other = (LdiInstruction)rhs;
            return _writeToHL == other._writeToHL;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_writeToHL);
        }

        @Override
        public String toString() {
            return _writeToHL ? "LDI (HL), A" : "LDI A, (HL)";
        }

        @Override
        public void execute(EmulatorState state) {
            ldAHL(state, _writeToHL);
            Register16.HL.set(state, (short) (Register16.HL.get(state) + 1));
        }
    }

    // Load A to/from HL and decrement HL.
    // _writeToHL = true -> LDI (HL), A
    // _writeToHL = false -> LDI A, (HL)
    public static class LddInstruction extends Instruction {
        private final boolean _writeToHL;

        private LddInstruction(boolean writeToHL) {
            _writeToHL = writeToHL;
        }

        public static LddInstruction lddFromHL() {
            return new LddInstruction(false);
        }

        public static LddInstruction lddToHL() {
            return new LddInstruction(true);
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            LddInstruction other = (LddInstruction)rhs;
            return _writeToHL == other._writeToHL;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_writeToHL);
        }

        @Override
        public String toString() {
            return _writeToHL ? "LDD (HL), A" : "LDD A, (HL)";
        }

        @Override
        public void execute(EmulatorState state) {
            ldAHL(state, _writeToHL);
            Register16.HL.set(state, (short)(Register16.HL.get(state) - 1));
        }
    }

    // Represents the LD A, ($FF00+C) and LD ($FF00+C), A instructions.
    public static class LdIoPortInstruction extends Instruction {
        private final boolean _writeToPort;

        private LdIoPortInstruction(boolean writeToPort) {
            _writeToPort = writeToPort;
        }

        public static LdIoPortInstruction LdFromIoPort() {
            return new LdIoPortInstruction(false);
        }

        public static LdIoPortInstruction LdToIoPort() {
            return new LdIoPortInstruction(true);
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            LdIoPortInstruction other = (LdIoPortInstruction)rhs;
            return _writeToPort == other._writeToPort;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_writeToPort);
        }

        @Override
        public String toString() {
            return _writeToPort ? "LD (FF00+C), A" : "LD A, (FF00+C)";
        }

        @Override
        public void execute(EmulatorState state) {
            short address = (short)(0xFF00 + Register8.C.get(state));
            SettableValueContainer<Byte> bytePtr = new BytePointer(address);

            if (_writeToPort)
                load8(state, bytePtr, Register8.A);
            else
                load8(state, Register8.A, bytePtr);
        }
    }

    public static class LdHLToSpInstruction extends Instruction {
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
            return "LD SP, HL";
        }

        @Override
        public void execute(EmulatorState state) {
            Register16.SP.set(state, Register16.HL.get(state));
        }
    }

    public static class LdSpToHLInstruction extends Instruction {
        private final byte _offset;

        public LdSpToHLInstruction(byte offset) {
            _offset = offset;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            LdSpToHLInstruction other = (LdSpToHLInstruction)rhs;
            return _offset == other._offset;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_offset);
        }

        @Override
        public String toString() {
            return String.format("LD HL, SP+%s", Util.byteToHexString(_offset));
        }

        @Override
        public void execute(EmulatorState state) {
            short oldValue = Register16.SP.get(state);
            short newValue = (short)(oldValue + _offset);
            Register16.HL.set(state, newValue);

            state.registerState.flags.setZ(0);
            state.registerState.flags.setN(0);
            state.registerState.flags.setH(
                    Util.add16WouldHalfCarry(oldValue, _offset) ? 1 : 0);
            state.registerState.flags.setC(
                    Util.add16WouldCarry(oldValue, _offset) ? 1 : 0);
        }
    }
}
