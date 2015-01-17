package cpu.disassembler;

import cpu.EmulatorState;

/**
 * Represents a single instruction.
 */
public abstract class Instruction {
    // Some instructions, e.g., jumps, may require additional cycles. Most
    // shouldn't.
    public int getAdditionalCycles(EmulatorState state) {
        return 0;
    }

    public abstract void execute(EmulatorState state);
}
