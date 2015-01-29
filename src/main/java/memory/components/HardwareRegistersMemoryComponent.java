package memory.components;

import cpu.EmulatorState;
import memory.components.hardware_registers.HardwareRegister;
import memory.components.hardware_registers.ReadonlyRegister;
import memory.components.hardware_registers.graphics.DmaRegister;
import memory.components.hardware_registers.graphics.LcdControllerRegister;
import memory.components.hardware_registers.graphics.PaletteRegister;
import memory.components.hardware_registers.graphics.StatRegister;
import util.Util;

/**
 * Represents the Memory-Mapped I/O set of hardware registers located in the
 * address range FF00..FF7F.
 */
public class HardwareRegistersMemoryComponent extends MemoryComponent {
    private final static int START_ADDRESS = 0xFF00;
    private final static int END_ADDRESS = 0xFF80;

    private final EmulatorState _emulatorState;
    private final HardwareRegister[] _hardwareRegisters;

    public final LcdControllerRegister lcdControllerRegister;
    public final StatRegister statRegister;
    public final HardwareRegister scrollY;
    public final HardwareRegister scrollX;
    public final ReadonlyRegister ly;
    public final HardwareRegister lyc;
    public final DmaRegister dmaRegister;
    public final PaletteRegister bgPalette;

    private final PaletteRegister _obj0Palette;
    private final PaletteRegister _obj1Palette;
    public final PaletteRegister[] objPalettes;

    public final HardwareRegister windowY;
    public final HardwareRegister windowX;

    public HardwareRegistersMemoryComponent(EmulatorState emulatorState) {
        _emulatorState = emulatorState;

        lcdControllerRegister = new LcdControllerRegister();
        statRegister = new StatRegister();
        scrollY = new HardwareRegister((short)0xFF42);
        scrollX = new HardwareRegister((short)0xFF43);
        ly = new ReadonlyRegister((short)0xFF44);
        lyc = new HardwareRegister((short)0xFF45);
        dmaRegister = new DmaRegister();
        bgPalette = new PaletteRegister((short)0xFF47);

        _obj0Palette = new PaletteRegister((short)0xFF48);
        _obj1Palette = new PaletteRegister((short)0xFF49);
        objPalettes = new PaletteRegister[] { _obj0Palette, _obj1Palette };

        windowY = new HardwareRegister((short)0xFF4A);
        windowX = new HardwareRegister((short)0xFF4B);

        _hardwareRegisters = new HardwareRegister[] {
                lcdControllerRegister,
                statRegister,
                scrollY,
                scrollX,
                ly,
                lyc
        };
    }

    @Override
    public boolean isResponsibleFor(short address) {
        return (address & 0xFFFF) >= START_ADDRESS &&
                (address & 0xFFFF) < END_ADDRESS;
    }

    // Asserts that there is AT MOST one register (not exactly one).
    // This is so that while the emulator is still being built, we don't
    // need to have placeholder registers for all addresses.
    // TODO(ddoucet): When all registers have been added, this should be
    // changed so that EXACTLY one register matches.
    private HardwareRegister findRegister(short address) {
        HardwareRegister ret = null;

        for (HardwareRegister reg : _hardwareRegisters) {
            if (reg.isResponsibleFor(address)) {
                if (ret != null) {
                    String error = String.format("Already found hardware " +
                            "register %s when %s matched (address %s)",
                            ret.getClass().getName(), reg.getClass().getName(),
                            Util.shortToHexString(address));
                    throw new IllegalStateException(error);
                }

                ret = reg;
            }
        }

        return ret;
    }

    @Override
    protected byte uncheckedRead(short address) {
        HardwareRegister reg = findRegister(address);

        if (reg == null) {
            int register = (address & 0xFFFF) - START_ADDRESS;
            System.out.println(String.format("Read from MMIO Register %d (address %s)",
                    register, Util.shortToHexString(address)));

            return 0;
        } else {
            return reg.read(address);
        }
    }

    @Override
    protected void uncheckedWrite(short address, byte value) {
        HardwareRegister reg = findRegister(address);

        if (reg == null) {
            int register = (address & 0xFFFF) - START_ADDRESS;
            System.out.println(String.format("Write %s to MMIO Register %d (address %s)",
                    Util.byteToHexString(value), register, Util.shortToHexString(address)));
        } else {
            reg.write(address, value);
        }
    }
}
