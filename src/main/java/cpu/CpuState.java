package cpu;

import memory.Memory;
import memory.MemoryBankController;

/**
 * TODO(ddoucet): is CpuState the best name for this? It should include
 * an MBC as well as a reference to the memory and all of the register state
 */
public class CpuState {
    public boolean interruptsEnabled;
    public final RegisterState registerState;
    public final Memory memory;

    public CpuState() {
        this(null);
    }

    public CpuState(MemoryBankController mbc) {
        interruptsEnabled = true;
        registerState = new RegisterState();
        memory = new Memory(mbc);
    }
}
