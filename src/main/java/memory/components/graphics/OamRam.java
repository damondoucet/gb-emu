package memory.components.graphics;

import memory.components.MemoryComponent;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
import org.junit.Assert;
import util.ByteScanner;
import util.Util;

import java.util.Arrays;

/**
 * Represents the OAM (sprite RAM) section of memory located at FE00..FE9F.
 */
public class OamRam extends MemoryComponent {
    private final short OAM_START = (short)0xFE00;
    private final short OAM_END = (short)0xFEA0;

    // TODO(ddoucet): SCREEN_WIDTH/HEIGHT doesn't belong here.
    private final static int SCREEN_WIDTH = 160;
    private final static int SCREEN_HEIGHT = 144;

    private final static int MAX_SPRITES_PER_LINE = 10;

    private final static int SPRITE_WIDTH = 8;
    private final static int SPRITE_BUFFER_HEIGHT = 16;

    private final static int NUM_SPRITES = 40;

    private final LcdControllerRegister _lcdc;

    public final Sprite[] sprites;

    // _yStart[i] is the first index at which a sprite has y-coordinate >= i.
    private final int[] _yStart;

    public OamRam(LcdControllerRegister lcdc) {
        _lcdc = lcdc;

        sprites = new Sprite[NUM_SPRITES];
        _yStart = new int[SCREEN_HEIGHT + SPRITE_BUFFER_HEIGHT];

        // Initialize all sprites to empty so that drawing doesn't throw a null
        // pointer exception.
        for (int i = 0; i < sprites.length; i++)
            sprites[i] = Sprite.zeroSprite(lcdc);
    }

    // Used with DMA; DMA takes a chunk of 160 bytes from RAM and copies it
    // into OAM, with sprites sorted. This reads the bytes from the scanner,
    // creates sprite objects, and then sorts them appropriately (also
    // creating the _yStart helper array).
    public void loadFromByteScanner(ByteScanner scanner) {
        for (int i = 0; i < sprites.length; i++)
            sprites[i] = Sprite.fromByteScanner(_lcdc, scanner);

        Arrays.sort(sprites);

        int currentY = 0;
        _yStart[0] = 0;

        int yStop = SCREEN_HEIGHT + SPRITE_BUFFER_HEIGHT - 1;

        for (int i = 0; i < sprites.length && currentY < yStop; i++) {
            while (currentY < (sprites[i].y & 0xFF) && currentY < yStop) {
                currentY++;
                _yStart[currentY] = i;
            }
        }

        // In case no sprites are at the bottom, set _yStart to be the end of
        // the sprite array.
        while (currentY < yStop) {
            currentY++;
            _yStart[currentY] = sprites.length;
        }
    }

    private int getYStart(int y) {
        return y < SCREEN_HEIGHT + SPRITE_BUFFER_HEIGHT ? _yStart[y] : sprites.length;
    }

    // Returns an array such that each element is either null or the sprite
    // that should be rendered at that index (x-coord) on the given y.
    // A null element means that no sprite was to be rendered at that coordinate.
    public Sprite[] getSpriteRow(int y) {
        // For some reason, a sprite at screen coordinate (0, 0) should have
        // sprite coordinates (8, 16). i.e., all y coordinates are shifted
        // up by 16.
        y += 16;

        Sprite[] row = new Sprite[SCREEN_WIDTH];

        if (!_lcdc.objOn.get())
            return row;  // all null spites, because OBJ (sprites) isn't on.

        // We want to render all sprites that would have come from above or are
        // actually supposed to be on this line.
        int spriteHeight = _lcdc.getSpriteHeight();
        int start = getYStart(y - spriteHeight + 1);

        int stop = Math.min(start + MAX_SPRITES_PER_LINE, getYStart(y + 1));

        for (int i = start; i < stop; i++) {
            // Add sprite i to the row
            // See comment above about screen coordinates vs. sprite coordinates
            // for why we subtract SPRITE_WIDTH.
            int spriteX = sprites[i].x & 0xFF;
            int xStart = Math.max(spriteX - SPRITE_WIDTH, 0);
            int xStop = Math.min(SCREEN_WIDTH, spriteX);

            for (int x = xStart; x < xStop; x++)
                if (row[x] == null || (row[x].x & 0xFF) > spriteX)
                    row[x] = sprites[i];
        }

        return row;
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= (OAM_START & 0xFFFF) &&
                (address & 0xFFFF) < (OAM_END & 0xFFFF);
    }

    @Override
    protected byte uncheckedRead(short address) {
        int index = (address & 0xFFFF) - (OAM_START & 0xFFFF);
        return sprites[index / 4].readByte(index % 4);
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        int index = (address & 0xFFFF) - (OAM_START & 0xFFFF);
        sprites[index / 4].writeByte(index % 4, value);
    }
}
