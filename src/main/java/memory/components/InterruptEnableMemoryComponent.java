package memory.components;

import cpu.EmulatorState;

/**
 * Represents the Interrupt Enable Register located at address FFFF.
 */
public class InterruptEnableMemoryComponent extends MemoryComponent {
    private byte _flag;
    private final EmulatorState _emulatorState;

    public InterruptEnableMemoryComponent(EmulatorState emulatorState) {
        _emulatorState = emulatorState;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) == 0xFFFF;
    }

    @Override
    protected byte uncheckedRead(short address) {
        return _flag;
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        // TODO(ddoucet): When Interrupts are ready, this should update which
        // ones are enabled in the EmulatorState.
        _flag = value;
    }
}
