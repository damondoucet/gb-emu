package memory.components.hardware_registers;

import util.Util;

/**
 * Represents one of the registers in the region FF00..FF7F.
 */
public class HardwareRegister {
    public final short address;
    public byte value;

    protected HardwareRegister(short address) {
        this.address = address;
        this.value = 0;
    }
    protected void setBit(int index, int value) {
        this.value = Util.setBit(this.value, index, value);
    }

    protected void setBit(int index, boolean value) {
        setBit(index, value ? 1 : 0);
    }

    protected int getBit(int index) {
        return Util.getBit(value, index);
    }

    protected boolean bitToBoolean(int index) {
        return getBit(index) == 1;
    }
}
