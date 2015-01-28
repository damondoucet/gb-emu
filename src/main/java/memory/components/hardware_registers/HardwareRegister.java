package memory.components.hardware_registers;

import memory.components.MemoryComponent;
import util.Util;

/**
 * Represents one of the registers in the region FF00..FF7F.
 */
public class HardwareRegister  extends MemoryComponent {
    private final short _address;
    private byte _value;

    protected HardwareRegister(short address) {
        _address = address;
        _value = 0;
    }
    protected void setBit(int index, int value) {
        _value = Util.setBit(_value, index, value);
    }

    protected void setBit(int index, boolean value) {
        setBit(index, value ? 1 : 0);
    }

    protected int getBit(int index) {
        return Util.getBit(_value, index);
    }

    protected boolean bitToBoolean(int index) {
        return getBit(index) == 1;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return _address == _address;
    }

    @Override
    protected byte uncheckedRead(short address) {
        return _value;
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        _value = value;
    }
}
