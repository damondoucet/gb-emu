package memory.components.hardware_registers.graphics;

import memory.components.hardware_registers.HardwareRegister;

import static com.google.common.base.Preconditions.*;

/**
 * Represents the LCDC Register
 */
public class LcdControllerRegister extends HardwareRegister {
    public final Flag bgDisplayOn;
    public final Flag objOn;
    private final Flag _objBlockComposition;
    private final Flag _bgCodeAreaStart;
    private final Flag _bgCharacterDataStart;
    public final Flag windowOn;
    private final Flag _windowCodeAreaStart;
    public final Flag lcdcOn;

    public LcdControllerRegister() {
        super((short)0xFF40);
        value = (byte)0x91;

        bgDisplayOn = new Flag(0);
        objOn = new Flag(1);
        _objBlockComposition = new Flag(2);
        _bgCodeAreaStart = new Flag(3);
        _bgCharacterDataStart = new Flag(4);
        windowOn = new Flag(5);
        _windowCodeAreaStart = new Flag(6);
        lcdcOn = new Flag(7);
    }

    private final static int SPRITE_HEIGHT_0 = 8;
    private final static int SPRITE_HEIGHT_1 = 16;

    public int getSpriteHeight() {
        return _objBlockComposition.getBit() == 0 ? SPRITE_HEIGHT_0 : SPRITE_HEIGHT_1;
    }

    // Useful for testing
    public void setSpriteHeight(int height) {
        checkArgument(height == SPRITE_HEIGHT_0 || height == SPRITE_HEIGHT_1,
                "%s not legal sprite height", height);

        _objBlockComposition.set(height == SPRITE_HEIGHT_1);
    }

    private final static short START_ADDR_0 = (short)0x9800;
    private final static short START_ADDR_1 = (short)0x9C00;

    public short getBgCodeAreaStart() {
        return _bgCodeAreaStart.getBit() == 0 ? START_ADDR_0 : START_ADDR_1;
    }

    public short getWindowCodeAreaStart() {
        return _windowCodeAreaStart.getBit() == 0 ? START_ADDR_0 : START_ADDR_1;
    }

    private final static short BG_CHAR_START_0 = (short)0x9000;
    private final static short BG_CHAR_START_1 = (short)0x8000;

    public short getBgCharDataStart() {
        return _bgCharacterDataStart.getBit() == 0 ? BG_CHAR_START_0 : BG_CHAR_START_1;
    }

    public boolean bgCharIndexIsSigned() {
        // The index is signed if it starts at 0x9000--the actual data resides
        // in 0x8800..0x97FF, where index 0 corresponds to 0x9000. A flag value
        // of 1 corresponds to the address range 0x8000..0x8FFF with index 0
        // corresponding to address 0x8000.
        return !_bgCharacterDataStart.get();
    }
}
