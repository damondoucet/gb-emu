package cpu.disassembler;

import cpu.CpuState;

/**
 * Represents a single instruction.
 */
public interface Instruction {
    void execute(CpuState state);
}
