package memory.components.hardware_registers;

import util.Util;

/**
 * Represents a register that is read-only to the CPU (although writable from
 * the emulator).
 */
public class ReadonlyRegister extends HardwareRegister {
    public ReadonlyRegister(short address) {
        super(address);
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        // TODO(ddoucet): Some registers are actually write-only, but I'm not
        // sure a ROM writing to the register should actually crash the
        // emulator. It might make more sense to just log and eat the write.
        String error = String.format("Illegal write to register at address " +
                "%s (value %s)",
                Util.shortToHexString(address), Util.byteToHexString(value));
        throw new UnsupportedOperationException(error);
    }
}
