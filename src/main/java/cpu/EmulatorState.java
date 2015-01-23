package cpu;

import cpu.disassembler.Instruction;
import cpu.disassembler.InstructionDecoder;
import cpu.disassembler.RootInstructionDecoder;
import cpu.disassembler.instruction_args.Register16;
import memory.CartridgeHeader;
import memory.Memory;
import memory.MemoryByteSource;
import memory.components.memory_bank_controllers.MemoryBankController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ByteScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * All state information for the currently running ROM/emulation.
 */
public class EmulatorState {
    // 1000 nanoseconds in a microsecond; each cycle takes 0.2384
    // microseconds or 238.4 nanoseconds
    private final static double NANO_SECONDS_PER_CYCLE = 238.4;

    public boolean interruptsEnabled;
    public final RegisterState registerState;
    public final Memory memory;

    private final List<Debugger> _debuggers;

    private final ByteScanner _scanner;
    private final InstructionDecoder _decoder;


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

        _debuggers = new ArrayList<Debugger>();
        _scanner = new ByteScanner(new MemoryByteSource(memory));
        _decoder = new RootInstructionDecoder();
    }

    public void addDebugger(Debugger debugger) {
        _debuggers.add(debugger);
    }

    public void run() {
        Register16.PC.set(this, (short)0x100);

        while (true)
            executeNextInstruction();
    }

    private void executeNextInstruction() {
        for (Debugger debugger : _debuggers)
            if (debugger.shouldBreak(this))
                debugger.onBreak(this);

        int oldPc = Register16.PC.get(this);
        _scanner.seek(oldPc);

        int cycles = _decoder.getMinimumCycles(_scanner);

        Instruction instr = _decoder.decodeNext(_scanner);
        cycles += instr.getAdditionalCycles(this);

        long startNanoSeconds = System.nanoTime();
        instr.execute(this);
        sleep(System.nanoTime() - startNanoSeconds, cycles);

        if (Register16.PC.get(this) == oldPc)
            Register16.PC.set(this, (short)_scanner.getIndex());
    }

    // Sleep so that the amount of time the instruction required on hardware
    // is actually spent.
    private void sleep(long expiredNanoSeconds, int instructionCycles) {
        int sleepNs = (int)(NANO_SECONDS_PER_CYCLE * instructionCycles - expiredNanoSeconds);

        // TODO(ddoucet): I should revisit this when things are looking a
        // little more fleshed out. It seems like we never sleep--that the
        // instructions are taking too long to execute. I wonder if that'll
        // actually be a problem when actually playing the games?
        if (sleepNs < 0)
            return;

        try {
            Thread.sleep(0, sleepNs);
        } catch (InterruptedException e) {
        }
    }

    public void halt() {
        // TODO(ddoucet)
        throw new NotImplementedException();
    }
}
