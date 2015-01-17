package cpu.disassembler;

import cpu.EmulatorState;

/**
 * Represents a single instruction.
 */
public interface Instruction {
    void execute(EmulatorState state);
}
