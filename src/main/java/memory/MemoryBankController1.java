package memory;

import com.google.common.hash.HashCode;

/**
 * MBC1 has two maximum-memory modes: 2mB ROM+8kB RAM or .5mB ROM+32kB RAM.
 *
 * It defaults to 2/8 on start. Writing a value (XXXXXXXS: x = ignored,
 * s = select) to anywhere in 0x6000..0x7FFF selects the memory model to use.
 * 0 corresponds to 2/8, 1 corresponds to .5/32.
 *
 * Writing a value (XXXBBBBB; x = ignored, b = bank select) into 0x2000..0x3FFF
 * selects the ROM bank to put at 0x4000..0x7FFF. Bank select values 0 and 1
 * both select the 1st bank (ROM bank 0 is only allowed to exist in the range
 * 0x0000..0x3FFF).
 *
 * Only 8kB of RAM are available at a time. RAM is located in the address range
 * 0xA000..0xC000.
 *
 * If the controller is in the 8kB RAM mode, then writing a value (XXXXXXBB;
 * x = ignored, b = bank select) into 0x4000..5FFF sets the two most
 * significant ROM address lines. (This means which half-mB chunk of ROM will
 * be used for loading a ROM bank; note that this is unnecessary in the other
 * mode because there is only one half-mB chunk of ROM in that case.)
 *
 * If the controller is in the 32kB RAM mode, then it uses the address range
 * 0x4000..5FFF for RAM selection instead. However, RAM selection must be
 * enabled before any writes to that region in this mode. RAM selection is
 * enabled by writing XXXX1010 to the 0x0000..0x1FFF region, and disabled
 * by writing any other value to that region.
 */
public class MemoryBankController1 implements MemoryBankController {
    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return HashCode.fromString(getClass().toString()).asInt();
    }

    @Override
    public String toString() {
        return getClass().toString();
    }
}
