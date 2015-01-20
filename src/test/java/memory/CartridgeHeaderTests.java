package memory;

import org.junit.Assert;
import org.junit.Test;
import util.ByteScanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The values for these ROMs were found using a hex editor and descriptions of
 * the ROM headers found on the internet.
 */
public class CartridgeHeaderTests {
    private ByteScanner fromResourceName(String resourceName) throws IOException {
        // Weird hack because on Windows, the path returned below has a leading /...
        // TODO(ddoucet): does this work on other platforms?
        String path = getClass().getClassLoader().getResource(resourceName).getPath();
        File file = new File(path);
        String realPath = file.getAbsolutePath().replace("%20", " ");

        return ByteScanner.fromFile(realPath);
    }

    private void testRomHeader(String path, CartridgeHeader expectedHeader) {
        try {
            ByteScanner scanner = fromResourceName(path);
            Assert.assertEquals(expectedHeader, CartridgeHeader.parse(scanner));
        } catch(IOException e) {
            Assert.fail("Uncaught IOException (does the file exist?): " + e.toString());
        }
    }

    @Test
    public void testAddamsFamily() {
        testRomHeader("AddamsFamily.gb", new CartridgeHeader(
                "ADDAMS FAMILY",
                false,
                (byte)0,
                false,
                false,
                new MemoryBankController1(),
                8,
                0,
                false,
                (byte)0x67,
                (byte)0x00,
                (byte)0xf0,
                (short)0xe13e
        ));
    }

    @Test
    public void testAsteroids() {
        testRomHeader("Asteroids.gb", new CartridgeHeader(
                "ASTEROIDS",
                false,
                (byte)0,
                false,
                false,
                null,
                2,
                0,
                false,
                (byte)0x79,
                (byte)0x00,
                (byte)0xbf,
                (short)0xfe94
        ));
    }

    @Test
    public void testContra() {
        testRomHeader("Contra.gb", new CartridgeHeader(
                "CONTRA",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                8,
                0,
                true,
                (byte)0xa4,
                (byte)0x00,
                (byte)0x79,
                (short)0x2935
        ));
    }

    @Test
    public void testKirbyDreamLand() {
        testRomHeader("KirbyDreamLand.gb", new CartridgeHeader(
                "KIRBY DREAM LAN",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                16,
                0,
                false,
                (byte)0x01,
                (byte)0x00,
                (byte)0x98,
                (short)0xadf9
        ));
    }

    @Test
    public void testMarioAndYoshi() {
        testRomHeader("MarioAndYoshi.gb", new CartridgeHeader(
                "MARIO & YOSHI",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                4,
                0,
                false,
                (byte)0x01,
                (byte)0x00,
                (byte)0x79,
                (short)0xe65b
        ));
    }

    @Test
    public void testMegaman() {
        testRomHeader("Megaman.gb", new CartridgeHeader(
                "MEGAMAN",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                16,
                0,
                false,
                (byte)0x08,
                (byte)0x00,
                (byte)0xe4,
                (short)0xc25b
        ));
    }

    @Test
    public void testPlayActionFootball() {
        testRomHeader("PlayActionFootball.gb", new CartridgeHeader(
                "DMG FOOTBALL",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                8,
                0,
                false,
                (byte)0x01,
                (byte)0x00,
                (byte)0x97,
                (short)0x2374
        ));
    }

    @Test
    public void testSuperMarioLand() {
        testRomHeader("SuperMarioLand.gb", new CartridgeHeader(
                "SUPER MARIOLAND",
                false,
                (byte)0x00,
                false,
                false,
                new MemoryBankController1(),
                4,
                0,
                true,
                (byte)0x01,
                (byte)0x00,
                (byte)0x9e,
                (short)0x416b
        ));
    }

    @Test
    public void testTetris() {
        testRomHeader("Tetris.gb", new CartridgeHeader(
                "TETRIS",
                false,
                (byte)0,
                false,
                false,
                null,
                (byte)2,
                (byte)0,
                true,
                (byte)0x01,
                (byte)0,
                (byte)0x0b,
                (short)-30283 // 0x89b5
        ));
    }
}
