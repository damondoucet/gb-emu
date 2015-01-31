package memory.components;

import static com.google.common.base.Preconditions.*;

/**
 * Handles a subset of the address space for the memory unit.
 */
public abstract class MemoryComponent {
    public byte read(short address) {
        checkArgument(isResponsibleFor(address));

        return uncheckedRead(address);
    }

    public void write(short address, byte value) {
        checkArgument(isResponsibleFor(address));

        uncheckedWrite(address, value);
    }

    /*
     * Whether this component is responsible for the given address.
     *
     * TODO(ddoucet): A lot of the extending classes of MemoryComponent could
     * use the Guava Range<T> class.
     */
    public abstract boolean isResponsibleFor(short address);

    /*
     * Assumes that isResponsibleFor(address) has already passed. Undefined
     * behavior in the event that it has not.
     *
     * Returns the byte located at the given address.
     */
    protected abstract byte uncheckedRead(short address);

    /*
     * Assumes that isResponsibleFor(address) has already passed. Undefined
     * behavior in the event that it has not.
     *
     * Performs a write at the given address. Note that this may not
     * necessarily set the value at that address (e.g., with memory bank
     * controllers and writing to the ROM).
     */
    protected abstract void uncheckedWrite(short address, byte value);
}
