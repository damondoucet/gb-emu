package memory.components.memory_bank_controllers;

import memory.components.Ram;
import util.Util;

import static com.google.common.base.Preconditions.*;

/**
 * MBC1 has two maximum-memory modes: 2mB ROM+8kB RAM or .5mB ROM+32kB RAM.
 *
 * It defaults to 2/8 on start. Writing a value (XXXXXXXS: x = ignored,
 * s = select) to anywhere in 0x6000..0x7FFF selects the memory model to use.
 * 0 corresponds to 2/8, 1 corresponds to .5/32.
 *
 * Writing a value (XXXBBBBB; x = ignored, b = bank select) into 0x2000..0x3FFF
 * selects low 5 bits of the the ROM bank to put at 0x4000..0x7FFF. Bank select
 * values 0 and 1 both select the 1st bank (ROM bank 0 is only allowed to exist
 * in the range 0x0000..0x3FFF).
 *
 * Only 8kB of RAM are available at a time. RAM is located in the address range
 * 0xA000..0xC000.
 *
 * If the controller is in the 8kB RAM mode, then writing a value (XXXXXXBB;
 * x = ignored, b = bank select) into 0x4000..5FFF sets the two most
 * significant ROM address lines. (This means which half-mB chunk of ROM will
 * be used for loading a ROM bank; note that this is unnecessary in the other
 * mode because there is only one half-mB chunk of ROM in that case.)
 *
 * If the controller is in the 32kB RAM mode, then it uses the address range
 * 0x4000..5FFF for RAM selection instead. However, RAM selection must be
 * enabled before any writes to that region in this mode. RAM selection is
 * enabled by writing XXXX1010 to the 0x0000..0x1FFF region, and disabled
 * by writing any other value to that region.
 */
public class MemoryBankController1 extends MemoryBankController {
    // In the event that there are only 2KB of RAM, we need to make sure that
    // any reads/writes to the RAM don't go beyond 2KB.
    private final int _ramKb;

    private boolean _is2mbMode;

    private Ram[] _ramBanks;
    private boolean _ramEnabled;

    private int _lowRomBankBits;

    // This register depends on which mode we're in
    private int _ramBankOrUpperRomBank;

    public MemoryBankController1(byte[] romBytes, int ramKb) {
        super(romBytes, ramKb != 0);

        _ramKb = ramKb;

        _is2mbMode = true;

        // We'll either see 2KB, 8KB, or 32KB of RAM. Each RAM bank is 8KB
        // large.
        // TODO(ddoucet): I'm pretty sure this is where save data goes, so at
        // some point, I should figure out how to work that back in here.
        _ramBanks = new Ram[(int)Math.ceil(ramKb / 8.0 / 1024)];
        for (int i = 0; i < _ramBanks.length; i++)
            _ramBanks[i] = new Ram(RAM_START, RAM_END);

        _ramEnabled = false;

        _lowRomBankBits = 0;
        _ramBankOrUpperRomBank = 0;
    }

    @Override
    protected int getRomBank() {
        int index = _lowRomBankBits;

        if (_is2mbMode)
            index |= _ramBankOrUpperRomBank << 5;

        return index;
    }

    private int getRamBank() {
        return _is2mbMode ? 0 : _ramBankOrUpperRomBank;
    }

    @Override
    protected byte readRam(short address) {
        checkState(_ramEnabled,
                "Attempt to read RAM (address %s) while RAM is disabled",
                Util.shortToHexString(address));

        checkArgument((address & 0xFFFF) - (RAM_START & 0xFFFF) < _ramKb * 1024);

        return _ramBanks[getRamBank()].read(address);
    }

    @Override
    protected void writeRam(short address, byte value) {
        checkState(_ramEnabled,
                "Attempt to write RAM (address %s, value %s) while RAM is disabled",
                Util.shortToHexString(address), Util.byteToHexString(value));

        checkArgument((address & 0xFFFF) - (RAM_START & 0xFFFF) < _ramKb * 1024);

        _ramBanks[getRamBank()].write(address, value);
    }

    @Override
    protected void writeRom(short address, byte value) {
        int unsignedAddr = address & 0xFFFF;

        if (unsignedAddr < 0x2000) {
            setRamEnable(value);
        } else if (unsignedAddr >= 0x2000 && unsignedAddr < 0x4000) {
            selectLowRomBankBits(value);
        } else if (unsignedAddr >= 0x4000 && unsignedAddr < 0x6000) {
            // Only two bits large
            _ramBankOrUpperRomBank = value & 0x3;
        } else if (unsignedAddr >= 0x6000 && unsignedAddr < 0x8000) {
            selectMemoryModel(value);
        } else {
            String error = String.format("Did not know how to handle writing " +
                "%s to %s", Util.byteToHexString(value), Util.shortToHexString(address));
            throw new UnsupportedOperationException(error);
        }
    }

    private void setRamEnable(byte value) {
        _ramEnabled = (value & 0xF) == 0xA;
    }

    private void selectLowRomBankBits(byte value) {
        _lowRomBankBits = value & 0x1F;

        if (_lowRomBankBits == 0)
            _lowRomBankBits = 1;
    }

    private void selectMemoryModel(byte value) {
        _is2mbMode = (value & 1) == 0;
    }
}
