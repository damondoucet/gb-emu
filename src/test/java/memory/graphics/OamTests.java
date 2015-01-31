package memory.graphics;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import memory.components.graphics.OamRam;
import memory.components.graphics.Sprite;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ArrayByteSource;
import util.ByteScanner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Tests the OamRam and Sprite classes in memory.components.graphics.
 *
 * Specifically, it tests that sprites are read correctly from a ByteScanner
 * and that sprites are correctly rendered to a row.
 *
 * There is a single byte[] that fuels data for all of the tests. First we test
 * that the bytes are parsed and sorted correctly as sprites, then we test
 * the rendering of several rows that test different conditions.
 *
 * We test sprites with both 8px height and 16px height (controlled through the
 * LCD Controller register). We space out different conditions for sprite
 * rendering on different rows and thus leave a buffer space of at least 16px
 * between rows (for the sprite).
 *
 * Remember that (0, 0) is completely hidden; to render a sprite in the top-
 * left corner, the coordinates should be (8, 16). The dimensions of the screen
 * are 160x144 (plus (8, 16) if you consider sprite coords).
 *
 * With sprite height = 16, a sprite with y-coordinate 16 would be rendered on
 * LCD (screen) rows 0..15, so 16 (the sprite y-coordinate) is the first row
 * on which the sprite is not rendered.
 *
 * In sorted order, the sprite coordinates we test are as follows:
 *
 *      (6, 10) -- Single sprite on a row; tests top-left cutoff
 *      (10, 34), (11, 35) -- one sprite above and to the left of another
 *      (10, 53), (11, 52) -- one sprite below and to the left of another
 *      (10, 70), (10, 71) -- one sprite directly above another
 *      (10, 89), (11, 89), (12, 89) -- sprites overlapping horizontally
 *      (10, 106), (11, 106), ..., (20, 106) -- test more than 10 sprites
 *      (10, 155), (165, 155) -- Non-overlapping sprites; tests bottom and
 *                               bottom-right cutoff
 *
 * Note that DATA must be padded with 0s so that it equals 160 bytes.
 *
 * Also note that the tests will initially assume sprite height is 8, because
 * that's how the register defaults.
 */
public class OamTests {
    // This is shuffled to test the OAM's sorting.
    private final static byte[] DATA = new byte[] {
            0, 0, 0, 0,
            106, 15, 6, 0,
            0, 0, 0, 0,
            106, 17, 9, 0,
            10, 6, 1, 0,
            106, 14, 5, 0,
            0, 0, 0, 0,
            52, 11, 11, 0,
            89, 10, 20, 0,
            106, 12, 10, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            70, 10, 2, 0,
            0, 0, 0, 0,
            106, 16, 22, 0,
            89, 12, 17, 0,
            0, 0, 0, 0,
            106, 18, 19, 0,
            0, 0, 0, 0,
            71, 10, 16, 0,
            35, 11, 3, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            106, 19, 13, 0,
            0, 0, 0, 0,
            34, 10, 21, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            89, 11, 4, 0,
            106, 20, 14, 0,
            (byte)155, (byte)165, 8, 0,
            106, 11, 18, 0,
            0, 0, 0, 0,
            (byte)155, 10, 7, 0,
            0, 0, 0, 0,
            106, 13, 1, 0,
            106, 10, 15, 0,
            53, 10, 12, 0,
            0, 0, 0, 0,
    };

    private final static short OAM_START = (short)0xFE00;

    private final static int SCREEN_WIDTH = 160;
    private final static int SCREEN_HEIGHT = 144;

    private Sprite[] sorted_sprites;

    private LcdControllerRegister _lcdc;
    private OamRam _oam;

    @Before
    public void init() {
        _lcdc = new LcdControllerRegister();
        _lcdc.objOn.set(true);

        _oam = new OamRam(_lcdc);

        ByteScanner scanner = new ByteScanner(new ArrayByteSource(DATA));
        _oam.loadFromByteScanner(scanner);

        // We can't make this const because the sprites depend on the LCDC reg
        sorted_sprites = new Sprite[] {
                new Sprite(_lcdc, (byte)10, (byte)6, (byte)1, false, false, false, 0),
                new Sprite(_lcdc, (byte)34, (byte)10, (byte)21, false, false, false, 0),
                new Sprite(_lcdc, (byte)35, (byte)11, (byte)3, false, false, false, 0),
                new Sprite(_lcdc, (byte)52, (byte)11, (byte)11, false, false, false, 0),
                new Sprite(_lcdc, (byte)53, (byte)10, (byte)12, false, false, false, 0),
                new Sprite(_lcdc, (byte)70, (byte)10, (byte)2, false, false, false, 0),
                new Sprite(_lcdc, (byte)71, (byte)10, (byte)16, false, false, false, 0),
                new Sprite(_lcdc, (byte)89, (byte)10, (byte)20, false, false, false, 0),
                new Sprite(_lcdc, (byte)89, (byte)11, (byte)4, false, false, false, 0),
                new Sprite(_lcdc, (byte)89, (byte)12, (byte)17, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)10, (byte)15, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)11, (byte)18, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)12, (byte)10, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)13, (byte)1, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)14, (byte)5, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)15, (byte)6, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)16, (byte)22, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)17, (byte)9, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)18, (byte)19, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)19, (byte)13, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)20, (byte)14, false, false, false, 0),
                new Sprite(_lcdc, (byte)155, (byte)10, (byte)7, false, false, false, 0),
                new Sprite(_lcdc, (byte)155, (byte)165, (byte)8, false, false, false, 0),
        };
    }

    @Test
    public void testLoadedCorrectly() {
        // There are 23 total sprites that we test; since there are 40 sprites
        // total, there are 17 zero sprites that should be sorted to the
        // beginning of the array.
        for (int i = 0; i < 17; i++)
            Assert.assertEquals(Sprite.zeroSprite(_lcdc), _oam.sprites[i]);

        for (int i = 0; i < 23; i++)
            Assert.assertEquals(sorted_sprites[i], _oam.sprites[i + 17]);
    }

    // Test that OamRam functions correctly as a read-only MemoryComponent.
    @Test
    public void testRereadingBytes() {
        // The first 17 * 4 bytes should be 0. The next 23 * 4 bytes should
        // come from the sprites.
        short address = (short)(OAM_START & 0xFFFF);
        for (int i = 0; i < 17 * 4; i++) {
            String error = String.format("Index %d incorrect", i);
            Assert.assertEquals(error, 0, _oam.read(address));
            address++;
        }

        for (int i = 0; i < 23; i++) {
            String error = String.format("Sprite %d incorrect", i);

            Assert.assertEquals(error, sorted_sprites[i].y, _oam.read(address));
            address++;

            Assert.assertEquals(error, sorted_sprites[i].x, _oam.read(address));
            address++;

            Assert.assertEquals(error, sorted_sprites[i].getTile(), _oam.read(address));
            address++;

            Assert.assertEquals(error, 0, _oam.read(address));
            address++;
        }
    }

    // The tests above all use a flags byte of 0 for simplicity; this checks
    // that reading the flags works correctly.
    @Test
    public void testFlagsByteCorrect() {
        Sprite sprite;

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, false, false, false, 0);
        Assert.assertEquals((byte)0x00, sprite.readByte(3));

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, false, false, false, 1);
        Assert.assertEquals((byte)0x10, sprite.readByte(3));

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, false, false, true, 0);
        Assert.assertEquals((byte)0x20, sprite.readByte(3));

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, false, true, false, 0);
        Assert.assertEquals((byte)0x40, sprite.readByte(3));

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, true, false, false, 0);
        Assert.assertEquals((byte)0x80, sprite.readByte(3));

        sprite = new Sprite(_lcdc, (byte)0, (byte)0, (byte)0, true, true, true, 1);
        Assert.assertEquals((byte)0xF0, sprite.readByte(3));
    }

    private void testRowsEmpty(List<Range<Integer>> rowRanges) {
        // row of null sprites
        Sprite[] expected = new Sprite[SCREEN_WIDTH];

        for (Range<Integer> rows : rowRanges)
            for (int row = rows.lowerEndpoint(); row <= rows.upperEndpoint(); row++)
                Assert.assertArrayEquals(String.format("Failed at y=%d", row),
                        expected, _oam.getSpriteRow(row));
    }

    @Test
    public void testEmptyRowsRendered() {
        // Empty rows when sprite height is 8
        List<Range<Integer>> emptyRows8 = Arrays.asList(
                Range.closed(2, 17),
                Range.closed(27, 35),
                Range.closed(45, 53),
                Range.closed(63, 72),
                Range.closed(81, 89),
                Range.closed(98, 138)
        );

        // Empty rows when sprite height is 16
        List<Range<Integer>> emptyRows16 = Arrays.asList(
                Range.closed(10, 17),
                Range.closed(35, 35),
                Range.closed(53, 53),
                Range.closed(71, 72),
                Range.closed(89, 89),
                Range.closed(106, 138)
        );

        // Test with sprite height = 8
        testRowsEmpty(emptyRows8);

        // Now test sprite height = 16
        _lcdc.setSpriteHeight(16);
        testRowsEmpty(emptyRows16);
    }

    // Test that a range of rows has the correct sprites, where the correct
    // row is defined by a map of x-coordinate ranges to Sprite objects.
    private void testRows(Map<Range<Integer>, Sprite> expectedMap, Range<Integer> rows) {
        Sprite[] expected = new Sprite[SCREEN_WIDTH];

        // Initialize the expected row
        for (Range<Integer> xRange : expectedMap.keySet()) {
            Sprite sprite = expectedMap.get(xRange);
            for (int x = xRange.lowerEndpoint(); x <= xRange.upperEndpoint(); x++)
                expected[x] = sprite;
        }

        for (int y = rows.lowerEndpoint(); y <= rows.upperEndpoint(); y++)
            Assert.assertArrayEquals(String.format("Failed at y=%d", y),
                    expected, _oam.getSpriteRow(y));
    }

    private void testRowsSingleSprite(Range<Integer> xRange, Sprite sprite, Range<Integer> rows) {
        Map<Range<Integer>, Sprite> expected =
                ImmutableMap.<Range<Integer>, Sprite>builder()
                    .put(xRange, sprite)
                    .build();

        testRows(expected, rows);
    }

    @Test
    public void testSingleSpriteRendered() {
        Sprite sprite = new Sprite(_lcdc, (byte)10, (byte)6, (byte)1, false, false, false, 0);

        testRowsSingleSprite(Range.closed(0, 5), sprite, Range.closed(0, 1));

        // Test with sprite height = 16
        _lcdc.setSpriteHeight(16);
        testRowsSingleSprite(Range.closed(0, 5), sprite, Range.closed(0, 9));
    }

    @Test
    public void testAboveLeftSpritesRendered() {
        Sprite s1 = new Sprite(_lcdc, (byte)34, (byte)10, (byte)21, false, false, false, 0);
        Sprite s2 = new Sprite(_lcdc, (byte)35, (byte)11, (byte)3, false, false, false, 0);

        // The row we expect when the sprites are overlapping.
        Map<Range<Integer>, Sprite> expectedOverlappingRow =
                ImmutableMap.<Range<Integer>, Sprite>builder()
                        .put(Range.closed(2, 9), s1)
                        .put(Range.closed(10, 10), s2)
                        .build();

        // Test the upper row where s2 isn't rendered. This should be true
        // whether sprite height is 8 or 16.
        testRowsSingleSprite(Range.closed(2, 9), s1, Range.closed(18, 18));

        // Test with sprite height = 8
        testRows(expectedOverlappingRow, Range.closed(19, 25));

        // Test the bottom row where s1 isn't rendered. This is dependent on
        // sprite height.
        testRowsSingleSprite(Range.closed(3, 10), s2, Range.closed(26, 26));

        // Now test with sprite height = 16.
        _lcdc.setSpriteHeight(16);

        testRowsSingleSprite(Range.closed(2, 9), s1, Range.closed(18, 18));
        testRows(expectedOverlappingRow, Range.closed(19, 33));
        testRowsSingleSprite(Range.closed(3, 10), s2, Range.closed(34, 34));
    }

    @Test
    public void testBelowLeftSpritesRendered() {
        Sprite s1 = new Sprite(_lcdc, (byte)52, (byte)11, (byte)11, false, false, false, 0);
        Sprite s2 = new Sprite(_lcdc, (byte)53, (byte)10, (byte)12, false, false, false, 0);

        // The row we expect when the sprites are overlapping.
        Map<Range<Integer>, Sprite> expectedOverlappingRow =
                ImmutableMap.<Range<Integer>, Sprite>builder()
                        .put(Range.closed(2, 9), s2)
                        .put(Range.closed(10, 10), s1)
                        .build();

        // Test the upper row where s2 isn't rendered. This should be true
        // whether sprite height is 8 or 16.
        testRowsSingleSprite(Range.closed(3, 10), s1, Range.closed(36, 36));

        // Test with sprite height = 8
        testRows(expectedOverlappingRow, Range.closed(37, 43));

        // Test the bottom row where s1 isn't rendered. This is dependent on
        // sprite height.
        testRowsSingleSprite(Range.closed(2, 9), s2, Range.closed(44, 44));

        // Now test with sprite height = 16.
        _lcdc.setSpriteHeight(16);

        testRowsSingleSprite(Range.closed(3, 10), s1, Range.closed(36, 36));
        testRows(expectedOverlappingRow, Range.closed(37, 51));
        testRowsSingleSprite(Range.closed(2, 9), s2, Range.closed(52, 52));
    }

    @Test
    public void testDirectlyAboveSpritesRendered() {
        Sprite s1 = new Sprite(_lcdc, (byte)70, (byte)10, (byte)2, false, false, false, 0);
        Sprite s2 = new Sprite(_lcdc, (byte)71, (byte)10, (byte)16, false, false, false, 0);

        // Test with sprite height = 8.

        // Test the upper rows where s2 isn't rendered.
        testRowsSingleSprite(Range.closed(2, 9), s1, Range.closed(54, 61));

        // Test the bottom row where s1 isn't rendered. This is dependent on
        // sprite height.
        testRowsSingleSprite(Range.closed(2, 9), s2, Range.closed(62, 62));

        // Now test with sprite height = 16.
        _lcdc.setSpriteHeight(16);

        testRowsSingleSprite(Range.closed(2, 9), s1, Range.closed(54, 69));
        testRowsSingleSprite(Range.closed(2, 9), s2, Range.closed(70, 70));
    }

    @Test
    public void testHorizontallyOverlappingSpritesRendered() {
        Sprite s1 = new Sprite(_lcdc, (byte)89, (byte)10, (byte)20, false, false, false, 0);
        Sprite s2 = new Sprite(_lcdc, (byte)89, (byte)11, (byte)4, false, false, false, 0);
        Sprite s3 = new Sprite(_lcdc, (byte)89, (byte)12, (byte)17, false, false, false, 0);

        // The row we expect when the sprites are overlapping.
        Map<Range<Integer>, Sprite> expectedOverlappingRow =
                ImmutableMap.<Range<Integer>, Sprite>builder()
                        .put(Range.closed(2, 9), s1)
                        .put(Range.closed(10, 10), s2)
                        .put(Range.closed(11, 11), s3)
                        .build();

        // Test with sprite height = 8
        testRows(expectedOverlappingRow, Range.closed(73, 80));

        // Now test with sprite height = 16.
        _lcdc.setSpriteHeight(16);
        testRows(expectedOverlappingRow, Range.closed(73, 88));
    }

    @Test
    public void testTooManySprites() {
        // Note that the sprite with x = 20 is left off because there are too
        // many sprites on this line.
        Sprite[] sprites = new Sprite[] {
                new Sprite(_lcdc, (byte)106, (byte)10, (byte)15, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)11, (byte)18, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)12, (byte)10, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)13, (byte)1, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)14, (byte)5, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)15, (byte)6, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)16, (byte)22, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)17, (byte)9, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)18, (byte)19, false, false, false, 0),
                new Sprite(_lcdc, (byte)106, (byte)19, (byte)13, false, false, false, 0),
        };

        Sprite[] row = new Sprite[SCREEN_WIDTH];

        // Add the first sprite
        for (int x = sprites[0].x - 8; x < sprites[0].x; x++)
            row[x] = sprites[0];

        // Add the rest of the sprites. Each sprite only gets one pixel where
        // it has priority (the 7 pixels to the left are dominated by the
        // sprite to the left of it).
        for (int i = 1; i < sprites.length; i++)
            row[sprites[i].x - 1] = sprites[i];

        // Test with sprite height = 8
        for (int y = 90; y < 98; y++)
            Assert.assertArrayEquals(String.format("Failed at y=%d", y),
                    row, _oam.getSpriteRow(y));

        // Test with sprite height = 16
        _lcdc.setSpriteHeight(16);
        for (int y = 90; y < 106; y++)
            Assert.assertArrayEquals(String.format("Failed at y=%d", y),
                    row, _oam.getSpriteRow(y));
    }

    @Test
    public void testNonoverlappingSpritesRendered() {
        Sprite s1 = new Sprite(_lcdc, (byte)155, (byte)10, (byte)7, false, false, false, 0);
        Sprite s2 = new Sprite(_lcdc, (byte)155, (byte)165, (byte)8, false, false, false, 0);

        Map<Range<Integer>, Sprite> expectedRow =
                ImmutableMap.<Range<Integer>, Sprite>builder()
                        .put(Range.closed(2, 9), s1)
                        .put(Range.closed(157, 159), s2)
                        .build();

        testRows(expectedRow, Range.closed(139, SCREEN_HEIGHT - 1));
    }
}
