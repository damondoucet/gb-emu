package cpu.disassembler.instruction_args;

import cpu.EmulatorState;

/**
 * Represents anything that could hold and modify a value for use by
 * instructions. Register8, Register16, and MemoryAddress all implement this.
 */
public interface SettableValueContainer<T> extends ValueContainer<T> {
    public void set(EmulatorState state, T value);
}
