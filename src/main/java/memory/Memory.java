package memory;

import cpu.EmulatorState;
import memory.components.*;
import memory.components.memory_bank_controllers.MemoryBankController;
import util.Util;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the address space of the Gameboy.
 *
 * The address space is broken into separate MemoryComponents, each one
 * responsible for a different subset of the address space. This is because
 * while some of the address space simply acts as RAM, other parts, like the
 * ROM (controlled by the MemoryBankController) acts
 */
public class Memory {
    private final List<MemoryComponent> _components;
    public final HardwareRegistersMemoryComponent regs;

    public Memory(EmulatorState state, MemoryBankController mbc) {
        Ram workRam = new Ram((short)0xC000, (short)0xE000);

        // TODO(ddoucet): Once Graphics is ready, the VRAM and OAM components
        // should be their own classes rather than simple RAM.
        regs = new HardwareRegistersMemoryComponent(state);

        // MBC handles ROM (0000..7FFF) as well as cartridge RAM (A000..BFFF)
        _components = Arrays.asList(
                mbc,
                new Ram((short)0x8000, (short)0xA000),  // VRAM
                workRam,
                new EchoRam(workRam),
                new Ram((short)0xFE00, (short)0xFF00),  // OAM/Sprite RAM
                // FEA0..FEFF is unusable
                regs,
                new Ram((short)0xFF80, (short)0xFFFF),  // HRAM
                new InterruptEnableMemoryComponent(state)
        );
    }

    private MemoryComponent findComponent(short address) {
        MemoryComponent ret = null;

        for (MemoryComponent component : _components) {
            if (component.isResponsibleFor(address)) {
                if (ret != null) {
                    String error = String.format("Already found component" +
                            "responsible for address %s: %s (second component" +
                            "%s)", Util.shortToHexString(address),
                            ret.getClass().getName(), component.getClass().getName());
                    throw new IllegalStateException(error);
                }

                ret = component;
            }
        }

        checkState(ret != null, "Unable to find MemoryComponent that can handle" +
                "address %s", Util.shortToHexString(address));
        return ret;
    }

    public void writeByte(short address, byte value) {
        findComponent(address).write(address, value);
    }

    public void writeShort(short address, short value) {
        writeByte(address, Util.shortToLowByte(value));
        writeByte((short)(address + 1), Util.shortToHighByte(value));
    }

    public byte readByte(short address) {
        return findComponent(address).read(address);
    }

    public short readShort(short address) {
        byte low = readByte(address);
        byte high = readByte((short)(address + 1));
        return Util.shortFromBytes(high, low);
    }
}
