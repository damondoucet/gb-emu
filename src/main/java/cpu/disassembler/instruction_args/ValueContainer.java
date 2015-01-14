package cpu.disassembler.instruction_args;

import cpu.CpuState;

/**
 * Represents anything that could hold a value for use by instructions.
 * Register8, Register16, and MemoryAddress all implement this.
 */
public interface ValueContainer<T> {
    public T get(CpuState state);
    public void set(CpuState state, T value);
}
