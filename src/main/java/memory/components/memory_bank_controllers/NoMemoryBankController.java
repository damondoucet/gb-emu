package memory.components.memory_bank_controllers;

import util.Util;

import static com.google.common.base.Preconditions.*;

/**
 * Handles reads from the ROM, and disallows reads/writes to the RAM and writes
 * to the ROM.
 */
public class NoMemoryBankController extends MemoryBankController {
    public NoMemoryBankController(byte[] romBytes) {
        super(romBytes, false);

        // TODO(ddoucet): This doesn't really seem right...
        // romBytes should only be null in a testing situation
        if (romBytes != null)
            checkArgument(romBytes.length == 0x8000);
    }

    @Override
    protected int getRomBank() {
        return 1;
    }

    @Override
    protected byte readRam(short address) {
        String error = String.format("Attempted to read RAM address %s" +
                        "when no MBC exists",
                Util.shortToHexString(address));
        throw new UnsupportedOperationException(error);
    }

    @Override
    protected void writeRam(short address, byte value) {
        String error = String.format("Attempted to write %s to RAM " +
                        "address %s when no MBC exists",
                Util.byteToHexString(value), Util.shortToHexString(address));
        throw new UnsupportedOperationException(error);
    }

    @Override
    protected void writeRom(short address, byte value) {
        String error = String.format("Attempted to write %s to ROM " +
                        "address %s when no MBC exists",
                Util.byteToHexString(value), Util.shortToHexString(address));
        throw new UnsupportedOperationException(error);
    }
}
