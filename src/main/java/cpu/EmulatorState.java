package cpu;

import memory.CartridgeHeader;
import memory.Memory;
import memory.components.memory_bank_controllers.MemoryBankController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * All state information for the currently running ROM/emulation.
 */
public class EmulatorState {
    public boolean interruptsEnabled;
    public final RegisterState registerState;
    public final Memory memory;

    // This constructor is only used for testing. Using this while trying to
    // actually run the emulator will likely result in a null pointer exception.
    public EmulatorState() {
        this((MemoryBankController)null);
    }

    public EmulatorState(CartridgeHeader header) {
        this(header.MemoryBankController);
    }

    private EmulatorState(MemoryBankController mbc) {
        interruptsEnabled = true;
        registerState = new RegisterState();

        memory = new Memory(this, mbc);
    }

    public void halt() {
        // TODO(ddoucet)
        throw new NotImplementedException();
    }
}
