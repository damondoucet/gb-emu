package memory.components.memory_bank_controllers;

import com.google.common.hash.HashCode;
import memory.components.MemoryComponent;
import util.Util;

import static com.google.common.base.Preconditions.*;

/**
 * Handles writes to the ROM, generally for switching ROM/RAM banks during
 * execution. Also handles writes/reads to the RAM, since the MBC is
 * responsible for switching RAM banks.
 *
 * For the sake of equality and toString, we act as if implementors are stateless.
 * This is because the CartridgeHeader's equality check only cares which type
 * of MemoryBankController was found, not the state that the MBC is manipulating.
 */
public abstract class MemoryBankController extends MemoryComponent {
    protected final static short RAM_START = (short)0xA000;
    protected final static short RAM_END = (short)0xC000;

    protected final byte[] _romBytes;
    protected final boolean _hasRam;

    protected MemoryBankController(byte[] romBytes, boolean hasRam) {
        // TODO(ddoucet): This doesn't really seem right...
        // romBytes should only be null in a testing situation
        if (romBytes != null)
            checkArgument(romBytes.length % 0x4000 == 0);

        _romBytes = romBytes;
        _hasRam = hasRam;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        int unsignedAddr = address & 0xFFFF;
        return unsignedAddr < 0x8000 ||
                isRamAddress(address);
    }

    private boolean isRamAddress(short address) {
        int unsignedAddr = address & 0xFFFF;
        return (unsignedAddr >= 0xA000 &&
                unsignedAddr < 0xC000);
    }

    @Override
    protected byte uncheckedRead(short address) {
        if (isRamAddress(address)) {
            checkState(_hasRam, "Attempt to read from RAM (address %s) without " +
                "any RAM in the cartridge", Util.shortToHexString(address));

            return readRam(address);
        } else {
            return readRom(address);
        }
    }

    private byte readRom(short address) {
        int index = address & 0xFFFF;

        if (index < 0x4000)
            return _romBytes[index];

        index -= 0x4000;
        return _romBytes[getRomBank() * 0x4000 + index];
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        if (isRamAddress(address)) {
            checkState(_hasRam, "Attempt to write to RAM (address %s, value %s) " +
                "without any RAM in the cartridge",
                    Util.shortToHexString(address), Util.byteToHexString(value));

            writeRam(address, value);
        } else {
            writeRom(address, value);
        }
    }

    protected abstract int getRomBank();
    protected abstract byte readRam(short address);
    protected abstract void writeRam(short address, byte value);
    protected abstract void writeRom(short address, byte value);

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return HashCode.fromString(toString()).asInt();
    }

    @Override
    public String toString() {
        return getClass().toString();
    }
}
