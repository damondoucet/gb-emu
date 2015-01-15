package cpu.disassembler.instructions;

import cpu.CpuState;
import cpu.disassembler.Instruction;

import java.util.Objects;

/**
 * Instructions for controlling the instruction pointer:
 *      NOP,
 *      JP, JR,
 *      CALL, RST
 */
public final class IPControlInstructions {
    private IPControlInstructions() {}

    public static class NopInstruction implements Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass().equals(rhs.getClass());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(toString());
        }

        @Override
        public String toString() {
            return "NOP";
        }

        @Override
        public void execute(CpuState state) {}
    }
}
