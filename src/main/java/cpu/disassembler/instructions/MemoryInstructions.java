package cpu.disassembler.instructions;

import cpu.CpuState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.SettableValueContainer;
import cpu.disassembler.instruction_args.ValueContainer;
import util.Util;

import java.util.Objects;

/**
 * Instructions dealing with memory:
 *      LD, LDH, PUSH, POP
 */
public final class MemoryInstructions {
    private MemoryInstructions() {}

    private static void updateSp(CpuState state, int delta) {
        short newValue = (short)(Register16.SP.get(state) + delta);
        Register16.SP.set(state, newValue);
    }

    public static void push(CpuState state, short value) {
        updateSp(state, -2);

        // When pushing, the most significant byte goes first on the stack.
        short sp = Register16.SP.get(state);
        state.memory.writeShort(sp, Util.swapBytes(value));
    }

    public static short pop(CpuState state) {
        short sp = Register16.SP.get(state);
        short returnValue = Util.swapBytes(state.memory.readShort(sp));

        updateSp(state, 2);
        return returnValue;
    }

    public static class PushInstruction implements Instruction {
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
        public void execute(CpuState state) {
            push(state, _container.get(state));
        }
    }

    public static class PopInstruction implements Instruction {
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
        public void execute(CpuState state) {
            _container.set(state, pop(state));
        }
    }
}
