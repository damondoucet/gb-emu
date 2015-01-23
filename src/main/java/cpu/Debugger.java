package cpu;

/**
 * Represents a class that can "debug" the Emulator. It is allowed to define
 * when the emulator should break, and what should happen when the emulator
 * does break for it.
 */
public interface Debugger {
    /*
     * Whether the emulator should call onBreak(state).
     */
    public boolean shouldBreak(EmulatorState state);

    /*
     * When shouldBreak(state) returns true, the emulator calls this method
     * then returns to executing.
     */
    public void onBreak(EmulatorState state);
}
