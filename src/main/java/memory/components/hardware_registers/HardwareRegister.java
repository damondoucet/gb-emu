package memory.components.hardware_registers;

import memory.components.MemoryComponent;
import util.Util;

/**
 * Represents one of the registers in the region FF00..FF7F.
 */
public class HardwareRegister extends MemoryComponent {
    private final short _address;
    public byte value;

    public HardwareRegister(short address) {
        _address = address;
        value = 0;
    }
    protected void setBit(int index, int value) {
        this.value = Util.setBit(this.value, index, value);
    }

    protected int getBit(int index) {
        return Util.getBit(value, index);
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return _address == _address;
    }

    @Override
    protected byte uncheckedRead(short address) {
        return value;
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        this.value = value;
    }

    public class Flag {
        private int _bitIndex;

        public Flag(int bitIndex) {
            _bitIndex = bitIndex;
        }

        public boolean get() {
            return getBit() == 1;
        }

        public int getBit() {
            return HardwareRegister.this.getBit(_bitIndex);
        }

        public void set(boolean value) {
            setBit(value ? 1 : 0);
        }

        public void setBit(int value) {
            HardwareRegister.this.setBit(_bitIndex, value);
        }
    }
}
