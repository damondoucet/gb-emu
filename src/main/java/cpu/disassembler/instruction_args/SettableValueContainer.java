package cpu.disassembler.instruction_args;

import cpu.CpuState;

/**
 * Represents anything that could hold and modify a value for use by
 * instructions. Register8, Register16, and MemoryAddress all implement this.
 */
public interface SettableValueContainer<T> extends ValueContainer<T> {
    public void set(CpuState state, T value);
}
