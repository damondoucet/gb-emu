package memory.components.graphics;

import memory.components.MemoryComponent;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
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
    private final static int SCREEN_WIDTH = 168;
    private final static int SCREEN_HEIGHT = 144;

    private final static int MAX_SPRITES_PER_LINE = 10;

    private final static int SPRITE_WIDTH = 8;

    private final static int NUM_SPRITES = 40;

    private final LcdControllerRegister _lcdc;

    public final Sprite[] sprites;

    // _yStart[i] is the first index at which a sprite has y-coordinate >= i.
    private final int[] _yStart;

    public OamRam(LcdControllerRegister lcdc) {
        _lcdc = lcdc;

        sprites = new Sprite[NUM_SPRITES];
        _yStart = new int[SCREEN_HEIGHT];

        // Initialize all sprites to empty so that drawing doesn't throw a null
        // pointer exception.
        for (int i = 0; i < sprites.length; i++)
            sprites[i] = Sprite.zeroSprite();
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
        for (int i = 0; i < sprites.length; i++) {
            while (currentY < sprites[i].y && currentY < SCREEN_HEIGHT) {
                currentY++;
                _yStart[currentY] = i;
            }
        }
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
        int start = _yStart[y - spriteHeight + 1];
        int stop = Math.min(start + MAX_SPRITES_PER_LINE, _yStart[y + 1]);

        for (int i = start; i < stop; i++) {
            // Add sprite i to the row
            // See comment above about screen coordinates vs. sprite coordinates
            // for why we subtract 8.
            int xStart = Math.max(sprites[i].x - 8, 0);
            int xStop = Math.min(SCREEN_WIDTH, sprites[i].x + SPRITE_WIDTH);
            for (int x = xStart; x < xStop; x++) {
                if (row[x] == null || row[x].x > sprites[i].x)
                    row[x] = sprites[i];
            }
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
        // TODO(ddoucet): same comment as ReadonlyRegister.
        String error = String.format("Illegal write to OAM address %s (value %s)",
                Util.shortToHexString(address), Util.byteToHexString(value));
        throw new UnsupportedOperationException(error);
    }
}
