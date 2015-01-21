package memory;

import com.google.common.base.MoreObjects;
import util.ByteScanner;
import util.Util;

import java.util.Arrays;
import java.util.Objects;

import static com.google.common.base.Preconditions.*;

/**
 * Data parsed from the ROM at addresses 0x104..0x14F.
 *
 * Only cartridge types 0x00 (ROM only) and 0x01 (ROM+MBC1) are supported. Most
 * surveyed Gameboy games were one of those two types. Several were of the type
 * ROM+MBC2+Battery, and none were other types (presumably the other cartridge
 * types are for Gameboy Color and Super Gameboy games).
 *
 * TODO(ddoucet): If time permits, adding MBC3, Battery, and RAM support would
 * be nice. (Pokemon Red uses value 0x13 - ROM+MBC3+RAM+Battery; Final Fantasy
 * III uses value 0x03 - ROM+MBC1+RAM+Battery)
 */
public class CartridgeHeader {
    public final String GameTitle;
    public final boolean IsGameboyColorGame;
    public final byte NewLicenseeCode;
    public final boolean IsSuperGameboyGame;
    public final boolean HasRam;
    public final MemoryBankController MemoryBankController;
    public final int NumRomBanks; // One bank is 16kB
    public final int NumRamKilobytes;
    public final boolean IsJapanese;
    public final byte OldLicenseeType;
    public final byte MaskRomVersionNumber;

    // This is required to be correct on the regular Gameboy.
    public final byte ComplementCheck;

    // This is ignored on the regular Gameboy. Described as the lower two bytes
    // of the sum of all bytes in the cartridge (except the two checksum bytes)
    public final short Checksum;

    // This is exposed for testing, but it's probably best to just use the
    // parse method.
    public CartridgeHeader(String gameTitle,
                           boolean isGameboyColorGame,
                           byte newLicenseeCode,
                           boolean isSuperGameboyGame,
                           boolean hasRam,
                           MemoryBankController memoryBankController,
                           int numRomBanks,
                           int numRamKilobytes,
                           boolean isJapanese,
                           byte oldLicenseeType,
                           byte maskRomVersionNumber,
                           byte complementCheck,
                           short checksum) {
        GameTitle = gameTitle;
        IsGameboyColorGame = isGameboyColorGame;
        NewLicenseeCode = newLicenseeCode;
        IsSuperGameboyGame = isSuperGameboyGame;
        HasRam = hasRam;
        MemoryBankController = memoryBankController;
        NumRomBanks = numRomBanks;
        NumRamKilobytes = numRamKilobytes;
        IsJapanese = isJapanese;
        OldLicenseeType = oldLicenseeType;
        MaskRomVersionNumber = maskRomVersionNumber;
        ComplementCheck = complementCheck;
        Checksum = checksum;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        CartridgeHeader other = (CartridgeHeader)obj;

        return GameTitle.equals(other.GameTitle) &&
                IsGameboyColorGame == other.IsGameboyColorGame &&
                NewLicenseeCode == other.NewLicenseeCode &&
                IsSuperGameboyGame == other.IsSuperGameboyGame &&
                HasRam == other.HasRam &&
                Objects.equals(MemoryBankController, other.MemoryBankController) &&
                NumRomBanks == other.NumRomBanks &&
                NumRamKilobytes == other.NumRamKilobytes &&
                IsJapanese == other.IsJapanese &&
                OldLicenseeType == other.OldLicenseeType &&
                MaskRomVersionNumber == other.MaskRomVersionNumber &&
                ComplementCheck == other.ComplementCheck &&
                Checksum == other.Checksum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(GameTitle,
                IsGameboyColorGame,
                NewLicenseeCode,
                IsSuperGameboyGame,
                HasRam,
                MemoryBankController,
                NumRomBanks,
                NumRamKilobytes,
                IsJapanese,
                OldLicenseeType,
                MaskRomVersionNumber,
                ComplementCheck,
                Checksum);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("GameTitle", GameTitle)
                .add("IsGameboyColorGame", IsGameboyColorGame)
                .add("NewLicenseeCode", NewLicenseeCode)
                .add("IsSuperGameboyGame", IsSuperGameboyGame)
                .add("HasRam", HasRam)
                .add("MemoryBankController", MemoryBankController)
                .add("NumRomBanks", NumRomBanks)
                .add("NumRamKilobytes", NumRamKilobytes)
                .add("IsJapanese", IsJapanese)
                .add("OldLicenseeType", OldLicenseeType)
                .add("MaskRomVersionNumber", MaskRomVersionNumber)
                .add("ComplementCheck", Util.byteToHexString(ComplementCheck))
                .add("Checksum", Util.shortToHexString(Checksum))
                .toString();
    }

    private final static int HEADER_START = 0x104;
    public static CartridgeHeader parse(ByteScanner scanner) {
        scanner.seek(HEADER_START);

        readNintendoGraphic(scanner);
        String gameTitle = readGameTitle(scanner);
        boolean isGameboyColorGame = readIsGameboyColorGame(scanner);
        byte newLicenseeCode = readNewLicenseeCode(scanner);
        boolean isSuperGameboyGame = readIsSuperGameboyGame(scanner);
        MemoryBankController mbc = readMemoryBankController(scanner);
        int numRomBanks = readNumRomBanks(scanner);
        int numRamKilobytes = readNumRamKilobytes(scanner);
        boolean isJapanese = readIsJapanese(scanner);
        byte oldLicenseeType = readOldLicenseeType(scanner);
        byte maskRomVersionNumber = readMaskRomVersionNumber(scanner);
        byte complementCheck = readComplementCheck(scanner);
        short checksum = readChecksum(scanner);

        checkArgument(!isGameboyColorGame, "GameboyColor not supported");
        checkArgument(!isSuperGameboyGame, "SuperGameboy not supported");

        byte expectedComplementCheck = computeComplementCheck(scanner);
        checkArgument(complementCheck == expectedComplementCheck,
            "Expected complement check value %s, got %s",
                Byte.toString(expectedComplementCheck),
                Byte.toString(complementCheck));

        return new CartridgeHeader(gameTitle,
                isGameboyColorGame,
                newLicenseeCode,
                isSuperGameboyGame,
                // TODO(ddoucet): this should be changed later if it's to be supported
                false /* has RAM */,
                mbc,
                numRomBanks,
                numRamKilobytes,
                isJapanese,
                oldLicenseeType,
                maskRomVersionNumber,
                complementCheck,
                checksum);
    }

    // Java doesn't support unsigned bytes, so we can't use the standard hex
    // values (they're interpreted as larger-typed values).
    private final static byte[] EXPECTED_NINTENDO_GRAPHIC = {
            -50, -19, 102, 102, -52, 13, 0, 11, 3, 115, 0, -125, 0, 12, 0, 13,
            0, 8, 17, 31, -120, -119, 0, 14, -36, -52, 110, -26, -35, -35, -39, -103,
            -69, -69, 103, 99, 110, 14, -20, -52, -35, -36, -103, -97, -69, -71, 51, 62
    };
    private static void readNintendoGraphic(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x104);
        byte[] bytes = scanner.readBytes(EXPECTED_NINTENDO_GRAPHIC.length);

        checkArgument(Arrays.equals(bytes, EXPECTED_NINTENDO_GRAPHIC),
                "Nintendo Graphic did not match expected bytes");
    }

    private final static int MAX_GAME_TITLE_LENGTH = 15;
    private static String readGameTitle(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x134);
        return new String(scanner.readBytes(MAX_GAME_TITLE_LENGTH))
                .replaceAll("\u0000", "");
    }

    // This value should be 0x80, but Java doesn't support unsigned bytes.
    // Any value besides COLOR_GB corresponds to non-gbc game
    private final static byte COLOR_GB = -128;
    private static boolean readIsGameboyColorGame(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x143);
        return scanner.readByte() == COLOR_GB;
    }

    private static byte readNewLicenseeCode(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x144);

        // The first byte is the high nibble, and the second byte is the low
        // nibble.
        byte highNibble = scanner.readByte();
        checkArgument(highNibble < 0x10,
                "Expected high nibble to be less than 0x10, got %s", highNibble);

        byte lowNibble = scanner.readByte();
        checkArgument(lowNibble < 0x10,
                "Expected low nibble to be less than 0x10, got %s", lowNibble);

        return (byte)((highNibble << 4) | lowNibble);
    }

    // Should be one of the following two values
    private final static byte REGULAR_GB = 0x00;
    private final static byte SUPER_GB = 0x03;
    private static boolean readIsSuperGameboyGame(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x146);

        byte data = scanner.readByte();
        checkArgument(data == REGULAR_GB || data == SUPER_GB,
                "Expected data to be REGULAR_GB or SUPER_GB, got %s", data);

        return data == SUPER_GB;
    }

    private final static byte ROM_ONLY = 0x00;
    private final static byte ROM_MBC1 = 0x01;
    private static MemoryBankController readMemoryBankController(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x147);

        byte data = scanner.readByte();
        checkArgument(data == ROM_ONLY || data == ROM_MBC1);

        return data == ROM_MBC1 ? new MemoryBankController1() : null;
    }

    private static int readNumRomBanks(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x148);
        byte data = scanner.readByte();

        // data = 0..6 -> number of banks is 2 ** (data + 1)
        if (data >= 0 && data < 7)
            return 1 << (data + 1);

        switch(data) {
            case 0x52:
                return 72;
            case 0x53:
                return 80;
            case 0x54:
                return 96;
            default:
                throw new IllegalArgumentException("Illegal value for ROM size: " + Byte.toString(data));
        }
    }

    private static int readNumRamKilobytes(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x149);
        byte data = scanner.readByte();

        checkArgument(data >= 0 && data < 5);

        if (data == 0)
            return 0;

        // 1 -> 2kB; 2 -> 8kB; 3 -> 32kB; 4 -> 128kB
        return 1 << (2 * data - 1);
    }

    private final static byte JAPANESE_DESTINATION = 0x00;
    private final static byte NONJAPANESE_DESTINATION = 0x01;
    private static boolean readIsJapanese(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x14A);
        byte data = scanner.readByte();

        checkArgument(data == JAPANESE_DESTINATION || data == NONJAPANESE_DESTINATION);

        return data == JAPANESE_DESTINATION;
    }

    private static byte readOldLicenseeType(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x14B);
        return scanner.readByte();
    }

    private static byte readMaskRomVersionNumber(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x14C);
        return scanner.readByte();
    }

    private static byte readComplementCheck(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x14D);
        return scanner.readByte();
    }

    private static short readChecksum(ByteScanner scanner) {
        checkState(scanner.getIndex() == 0x14E);

        return Util.shortFromBytes(scanner.readByte(), scanner.readByte());
    }

    // Described as 0xE7 minus the lower byte of the sum of the bytes in
    // addresses [0x134, 0x14D) in the cartridge.
    private static byte computeComplementCheck(ByteScanner scanner) {
        scanner.seek(0x134);

        byte sum = 0;
        for (int i = 0x134; i < 0x14D; i++)
            sum += scanner.readByte();

        return (byte)(0xE7 - sum);
    }
}
