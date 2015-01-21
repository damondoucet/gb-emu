package memory.components;

import cpu.EmulatorState;
import util.Util;

/**
 * Represents the Memory-Mapped I/O set of hardware registers located in the
 * address range FF00..FF7F.
 */
public class HardwareRegistersMemoryComponent extends MemoryComponent {
    private final static int START_ADDRESS = 0xFF00;
    private final static int END_ADDRESS = 0xFF80;

    private final EmulatorState _emulatorState;

    public HardwareRegistersMemoryComponent(EmulatorState emulatorState) {
        _emulatorState = emulatorState;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= START_ADDRESS &&
                (address & 0xFFFF) < END_ADDRESS;
    }

    @Override
    protected byte uncheckedRead(short address) {
        // TODO(ddoucet)
        int register = address - START_ADDRESS;
        System.out.println(String.format("Read from MMIO Register %d (address %s)",
                register, Util.shortToHexString(address)));

        return 0;
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        // TODO(ddoucet)
        int register = address - START_ADDRESS;
        System.out.println(String.format("Write %s to MMIO Register %d (address %s)",
                Util.byteToHexString(value), register, Util.shortToHexString(address)));

    }
}
