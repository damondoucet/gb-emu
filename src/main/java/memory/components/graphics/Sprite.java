package memory.components.graphics;

import com.google.common.collect.ComparisonChain;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
import util.ByteScanner;
import util.Util;

import static com.google.common.base.Preconditions.*;

/**
 * Represents a single sprite that resides in OAM.
 */
public class Sprite implements Comparable<Sprite> {
    public final byte y;
    public final byte x;
    public final byte tile;
    public final boolean priority;
    public final boolean yflip;
    public final boolean xflip;
    public final int palette;

    private Sprite(byte y, byte x, byte tile,
                  boolean priority, boolean yflip,
                  boolean xflip, int palette) {
        this.y = y;
        this.x = x;
        this.tile = tile;
        this.priority = priority;
        this.yflip = yflip;
        this.xflip = xflip;
        this.palette = palette;
    }

    public static Sprite zeroSprite() {
        return new Sprite((byte)0, (byte)0, (byte)0, false, false, false, 0);
    }

    // Four bytes: y, x, tile, and flags.
    // Flags: bit 7 - priority; bit 6 - y flip; bit 5 - xflip; bit 4 - palette
    // All other bits unused.
    public static Sprite fromByteScanner(LcdControllerRegister lcdc, ByteScanner scanner) {
        byte y = scanner.readByte();
        byte x = scanner.readByte();

        // When sprites are 16px tall, the least significant bit of tile index
        // is treated as 0 (there are half as many sprites because they are
        // twice as tall).
        byte tile = scanner.readByte();
        if (lcdc.getSpriteHeight() == 16)
            tile &= ~1;

        byte flags = scanner.readByte();

        boolean priority = Util.getBit(flags, 7) == 1;
        boolean yflip = Util.getBit(flags, 6) == 1;
        boolean xflip = Util.getBit(flags, 5) == 1;
        int palette = Util.getBit(flags, 4);

        return new Sprite(y, x, tile, priority, yflip, xflip, palette);
    }

    // index is 0..3, which byte of the Sprite is to be read.
    public byte readByte(int index) {
        checkArgument(index >= 0 && index < 3);

        if (index == 0) {
            return y;
        } else if (index == 1) {
            return x;
        } else if (index == 2) {
            return tile;
        } else {  // index == 3
            int b7 = priority ? 1 : 0;
            int b6 = yflip ? 1 : 0;
            int b5 = xflip ? 1 : 0;
            return (byte)(((((((b7 << 1) | b6) << 1) | b5) << 1) | palette) << 4);
        }
    }

    @Override
    public int compareTo(Sprite rhs) {
        return ComparisonChain.start()
                .compare(y, rhs.y)
                .compare(x, rhs.x)
                .result();
    }
}
