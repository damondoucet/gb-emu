package cpu;

import memory.Memory;
import memory.MemoryBankController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * All state information for the currently running ROM/emulation.
 */
public class EmulatorState {
    public boolean interruptsEnabled;
    public final RegisterState registerState;
    public final Memory memory;

    public EmulatorState() {
        this(null);
    }

    public EmulatorState(MemoryBankController mbc) {
        interruptsEnabled = true;
        registerState = new RegisterState();
        memory = new Memory(mbc);
    }

    public void halt() {
        // TODO(ddoucet)
        throw new NotImplementedException();
    }
}
