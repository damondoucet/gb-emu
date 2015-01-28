package memory.components;

import cpu.EmulatorState;
import memory.components.hardware_registers.HardwareRegister;
import util.Util;

/**
 * Represents the Memory-Mapped I/O set of hardware registers located in the
 * address range FF00..FF7F.
 */
public class HardwareRegistersMemoryComponent extends MemoryComponent {
    private final static int START_ADDRESS = 0xFF00;
    private final static int END_ADDRESS = 0xFF80;

    private final EmulatorState _emulatorState;
    private final HardwareRegister[] _hardwareRegisters;

    public HardwareRegistersMemoryComponent(EmulatorState emulatorState) {
        _emulatorState = emulatorState;
        _hardwareRegisters = new HardwareRegister[] {

        };
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= START_ADDRESS &&
                (address & 0xFFFF) < END_ADDRESS;
    }

    // Asserts that there is AT MOST one register (not exactly one).
    // This is so that while the emulator is still being built, we don't
    // need to have placeholder registers for all addresses.
    // TODO(ddoucet): When all registers have been added, this should be
    // changed so that EXACTLY one register matches.
    private HardwareRegister findRegister(short address) {
        HardwareRegister ret = null;

        for (HardwareRegister reg : _hardwareRegisters) {
            if (reg.isResponsibleFor(address)) {
                if (ret != null) {
                    String error = String.format("Already found hardware " +
                            "register %s when %s matched (address %s)",
                            ret.getClass().getName(), reg.getClass().getName(),
                            Util.shortToHexString(address));
                    throw new IllegalStateException(error);
                }

                ret = reg;
            }
        }

        return ret;
    }

    @Override
    protected byte uncheckedRead(short address) {
        HardwareRegister reg = findRegister(address);

        if (reg == null) {
            int register = (address & 0xFFFF) - START_ADDRESS;
            System.out.println(String.format("Read from MMIO Register %d (address %s)",
                    register, Util.shortToHexString(address)));

            return 0;
        } else {
            return reg.read(address);
        }
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        HardwareRegister reg = findRegister(address);

        if (reg == null) {
            int register = (address & 0xFFFF) - START_ADDRESS;
            System.out.println(String.format("Write %s to MMIO Register %d (address %s)",
                    Util.byteToHexString(value), register, Util.shortToHexString(address)));
        } else {
            reg.write(address, value);
        }
    }
}
