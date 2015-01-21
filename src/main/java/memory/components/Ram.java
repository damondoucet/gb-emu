package memory.components;

/**
 * MemoryComponent that represents some form of RAM.
 */
public class Ram extends MemoryComponent {
    public final int start;
    public final int end;

    protected final byte[] bytes;

    // Represents RAM for the address range [startAddress, endAddress)
    public Ram(short startAddress, short endAddress) {
        start = startAddress & 0xFFFF;
        end = endAddress & 0xFFFF;

        bytes = new byte[endAddress - startAddress];
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= start &&
                (address & 0xFFFF) < end;
    }

    @Override
    protected byte uncheckedRead(short address) {
        return bytes[(address & 0xFFFF) - start];
    }

    @Override
    protected void uncheckedWrite(short address, byte value){
        bytes[(address & 0xFFFF) - start] = value;
    }
}
