package memory.components.hardware_registers.graphics;

import memory.components.hardware_registers.HardwareRegister;

/**
 * Represents one of the palette registers (BGP, OBP0, and OBP1).
 *
 * Palette registers are split into four sets of two bits, each set
 * corresponding to the color for that given index.
 *
 * e.g. the value
 *      00100111
 * would be split into 00 (color 3), 10 (color 2), 01 (color 1), and 11
 * (color 0).
 */
public class PaletteRegister extends HardwareRegister {
    public PaletteRegister(short address) {
        super(address);
    }

    public int getColor(int index) {
        return (value >> (index * 2)) & 0x3;
    }
}
