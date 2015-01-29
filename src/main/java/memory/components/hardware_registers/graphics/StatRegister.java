package memory.components.hardware_registers.graphics;

import memory.components.hardware_registers.HardwareRegister;

/**
 * Represents the STAT register at 0xFF41.
 */
public class StatRegister extends HardwareRegister {
    public enum GpuMode {
        HBlank,  // when all display RAM is usable by the CPU
        VBlank,
        ScanningOamRam,
        TransferringDataToLcd
    }

    public final Flag lyMatch;
    public final Flag hblankInterruptDesired;
    public final Flag vblankInterruptDesired;
    public final Flag scanningOamInterruptDesired;
    public final Flag lyMatchInterruptDesired;

    public StatRegister() {
        super((short)0xFF41);
        lyMatch = new Flag(2);
        hblankInterruptDesired = new Flag(3);
        vblankInterruptDesired = new Flag(4);
        scanningOamInterruptDesired = new Flag(5);
        lyMatchInterruptDesired = new Flag(6);
    }

    // Take the mode bits (the two least significant bits) from bitsFrom and
    // set those values in valueTo. Return the result.
    private static int moveModeBits(int bitsFrom, int valueTo) {
        int from = bitsFrom & 0x03;
        return (valueTo & ~0x03) | from;
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        super.uncheckedWrite(address, (byte)moveModeBits(this.value, value));
    }

    public void setGpuMode(GpuMode mode) {
        value = (byte)(moveModeBits(mode.ordinal(), value));
    }
}
