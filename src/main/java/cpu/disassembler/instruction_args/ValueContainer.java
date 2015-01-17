package cpu.disassembler.instruction_args;

import cpu.EmulatorState;

/**
 * Represents anything that can hold a value. SettableValueContainer implements
 * this (e.g. registers), as do ByteConst and ShortConst.
 */
public interface ValueContainer<T> {
    public T get(EmulatorState state);
}
