package memory.components;

/**
 * Represents the address range E000..FDFF. Reading provides the same values as
 * the given RAM, and writing will write to the corresponding location in the
 * RAM.
 */
public class EchoRam extends MemoryComponent {
    private final static int START_ADDRESS = 0xE000;
    private final static int END_ADDRESS = 0xFE00;

    private final Ram _ram;

    public EchoRam(Ram ram) {
        _ram = ram;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= START_ADDRESS &&
                (address & 0xFFFF) < END_ADDRESS;
    }

    @Override
    protected byte uncheckedRead(short address) {
        return _ram.read((short)(address - START_ADDRESS + _ram.start));
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        _ram.write((short)(address - START_ADDRESS + _ram.start), value);
    }
}
