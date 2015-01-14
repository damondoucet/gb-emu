package cpu;

/**
 * TODO(ddoucet): is CpuState the best name for this? It should include
 * an MBC as well as a reference to the memory and all of the register state
 */
public class CpuState {
    public final RegisterState registerState;

    public CpuState() {
        registerState = new RegisterState();
    }
}
