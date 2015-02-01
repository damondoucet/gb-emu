package memory.components.graphics;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
import util.ByteScanner;
import util.Util;

import java.util.Objects;

import static com.google.common.base.Preconditions.*;

/**
 * Represents a single sprite that resides in OAM.
 */
public class Sprite implements Comparable<Sprite> {
    private final LcdControllerRegister _lcdc;
    public byte y;
    public byte x;
    private byte _tile;
    public boolean priority;
    public boolean yflip;
    public boolean xflip;
    public int palette;

    public Sprite(LcdControllerRegister lcdc,
                  byte y, byte x, byte tile,
                  boolean priority, boolean yflip,
                  boolean xflip, int palette) {
        _lcdc = lcdc;
        this.y = y;
        this.x = x;
        _tile = tile;
        this.priority = priority;
        this.yflip = yflip;
        this.xflip = xflip;
        this.palette = palette;
    }

    public Sprite(LcdControllerRegister lcdc,
                  byte y, byte x, byte tile, byte flags) {
        _lcdc = lcdc;
        this.y = y;
        this.x = x;
        _tile = tile;
        setFlagsByte(flags);
    }

    public byte getTile() {
        byte tile = _tile;

        // When sprites are 16px tall, the least significant bit of tile index
        // is treated as 0 (there are half as many tiles because they are
        // twice as tall).
        if (_lcdc.getSpriteHeight() == 16)
            tile &= ~1;

        return tile;
    }

    public static Sprite zeroSprite(LcdControllerRegister lcdc) {
        return new Sprite(lcdc, (byte)0, (byte)0, (byte)0, false, false, false, 0);
    }

    public void setFlagsByte(byte flags) {
        priority = Util.getBit(flags, 7) == 1;
        yflip = Util.getBit(flags, 6) == 1;
        xflip = Util.getBit(flags, 5) == 1;
        palette = Util.getBit(flags, 4);
    }

    // Four bytes: y, x, tile, and flags.
    // Flags: bit 7 - priority; bit 6 - y flip; bit 5 - xflip; bit 4 - palette
    // All other bits unused.
    public static Sprite fromByteScanner(LcdControllerRegister lcdc, ByteScanner scanner) {
        byte y = scanner.readByte();
        byte x = scanner.readByte();
        byte tile = scanner.readByte();
        byte flags = scanner.readByte();


        return new Sprite(lcdc, y, x, tile, flags);
    }

    // index is 0..3, which byte of the Sprite is to be read.
    public byte readByte(int index) {
        checkArgument(index >= 0 && index <= 3,
                "%s not legal argument to readByte", index);

        if (index == 0) {
            return y;
        } else if (index == 1) {
            return x;
        } else if (index == 2) {
            return _tile;
        } else {  // index == 3
            int b7 = priority ? 1 : 0;
            int b6 = yflip ? 1 : 0;
            int b5 = xflip ? 1 : 0;
            return (byte)(((((((b7 << 1) | b6) << 1) | b5) << 1) | palette) << 4);
        }
    }

    public void writeByte(int index, byte value) {
        checkArgument(index >= 0 && index <= 3,
                "%s not legal argument to readByte", index);

        if (index == 0) {
            y = value;
        } else if (index == 1) {
            x = value;
        } else if (index == 2) {
            _tile = value;
        } else {  // index == 3
            setFlagsByte(value);
        }
    }

    private int asInt() {
        int res = 0;
        for (int i = 0; i < 4; i++)
            res = (res << 8) | (readByte(i) & 0xFF);

        return res;
    }

    @Override
    public int compareTo(Sprite rhs) {
        return asInt() - rhs.asInt();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !getClass().equals(o.getClass()))
            return false;

        return asInt() == ((Sprite)o).asInt();
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x, _tile, priority, yflip, xflip, palette);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("y", y & 0xFF)
                .add("x", x & 0xFF)
                .add("tile", _tile)
                .add("priority", priority)
                .add("yflip", yflip)
                .add("xflip", xflip)
                .add("palette", palette)
                .toString();
    }
}
