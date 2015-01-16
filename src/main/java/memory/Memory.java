package memory;

/**
 * Represents the address space of the Gameboy.
 *
 * TODO(ddoucet)
 */
public class Memory {
    private final MemoryBankController _mbc;

    public Memory(MemoryBankController mbc) {
        _mbc = mbc;
    }

    public void writeByte(short address, byte value) {

    }

    public void writeShort(short address, short value) {

    }

    public byte readByte(short address) {
        return 0;
    }

    public short readShort(short address) {
        return 0;
    }
}
