package memory.components.hardware_registers.graphics;

import memory.components.hardware_registers.HardwareRegister;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents the register used for initiating DMA transfers (the way that a
 * ROM transfers data from RAM to OAM).
 */
public class DmaRegister extends HardwareRegister {
    public DmaRegister() {
        super((short)0xFF46);
    }

    @Override
    protected byte uncheckedRead(short address) {
        // TODO(ddoucet): See comment in ReadonlyRegister about maybe just
        // logging and eating the read.
        throw new UnsupportedOperationException("Attempt to read from DMA register");
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        // TODO(ddoucet)
        throw new NotImplementedException();
    }
}
