package tools;

import cpu.Debugger;
import cpu.EmulatorState;
import cpu.Flags;
import cpu.disassembler.InstructionDecoder;
import cpu.disassembler.RootInstructionDecoder;
import cpu.disassembler.instruction_args.Register16;
import memory.CartridgeHeader;
import memory.MemoryByteSource;
import util.ByteScanner;
import util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Debugs a ROM.
 *
 * Commands:
 *     pd aaaa -- Print decimal byte value located at address aaaa
 *     px aaaa -- Print hex byte value located at address aaaa
 *     pi aaaa -- Print instruction located at address aaaa
 *     b aaaa -- Set a breakpoint at address aaaa; prints breakpoint id
 *     d i -- Delete breakpoint i
 *     s -- Step through the next instruction
 *     c -- Continue until the next breakpoint
 */
public class RomDebugger implements Debugger {
    private final static int NUM_INSTRUCTIONS_PRINTED = 5;
    private final static String ERROR_FORMAT = "Unable to parse %s";

    private final BufferedReader _reader;
    private final InstructionDecoder _decoder;

    private final String _path;
    private final Map<Integer, Short> _breakpoints;
    private int _maxBreakpointSeen;

    private boolean _isStepping;

    public RomDebugger(BufferedReader reader, String path) {
        _reader = reader;
        _decoder = new RootInstructionDecoder();

        _path = path;
        _breakpoints = new HashMap<Integer, Short>();
        _maxBreakpointSeen = 0;

        // Break before first instruction
        _isStepping = true;
    }

    private static EmulatorState stateFromFile(String path) throws IOException {
        byte[] bytes = Util.bytesFromFile(path);

        CartridgeHeader header = CartridgeHeader.parse(bytes);
        return new EmulatorState(header);
    }

    public void run() throws IOException {
        EmulatorState state = stateFromFile(_path);
        state.addDebugger(this);

        state.run();
    }

    private boolean hasBreakpointAtAddress(EmulatorState state, short addr) {
        for (Short breakpoint : _breakpoints.values())
            if (addr == breakpoint)
                return true;

        return false;
    }

    @Override
    public boolean shouldBreak(EmulatorState state) {
        return _isStepping ||
                hasBreakpointAtAddress(state, Register16.PC.get(state));
    }

    private String readLine() {
        try {
            return _reader.readLine();
        } catch(IOException e) {
            System.out.println(e.toString());
            System.exit(1);
            return "";
        }
    }

    @Override
    public void onBreak(EmulatorState state) {
        printState(state);

        while (true) {
            System.out.print("> ");
            String input = readLine().toLowerCase().trim();

            if (input.equals("s")) {
                // Step
                _isStepping = true;
                return;
            } else if (input.equals("c")) {
                // Continue
                _isStepping = false;
                return;
            } else if (input.startsWith("p")) {
                handlePrint(state, input);
            } else if (input.startsWith("b")) {
                handleBreakpoint(input);
            } else if (input.startsWith("d")) {
                handleDeleteBreakpoint(input);
            }
        }
    }

    // Returns null if the text isn't an integer or doesn't fall within the
    // bounds.
    private Integer tryParse(String text, boolean hex, int min, int max) {
        try {
            int x = Integer.parseInt(text, hex ? 16 : 10);

            if (x < min || x > max) {
                System.out.println(String.format("Number did not fit in range: %d to %d",
                        min, max));
                return null;
            }

            return x;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + text);
            return null;
        }
    }

    private Short tryParseAddr(String text) {
        if (text.length() != 4) {
            System.out.println("Addresses must be exactly 4 hex digits");
            return null;
        }

        return (short)(int)tryParse(text, true, 0, 0xFFFF);
    }

    private void handlePrint(EmulatorState state, String input) {
        if (input.charAt(2) != ' ') {
            System.out.println(String.format(ERROR_FORMAT, input));
            return;
        }

        Short addr = tryParseAddr(input.substring(3));
        if (addr == null)
            return;

        if (input.charAt(1) == 'x') {
            byte value = state.memory.readByte(addr);
            System.out.println(Util.byteToHexString(value));
        } else if (input.charAt(1) == 'd') {
            byte value = state.memory.readByte(addr);
            System.out.println(value);
        } else if (input.charAt(1) == 'i') {
            ByteScanner scanner = new ByteScanner(new MemoryByteSource(state.memory));
            scanner.seek(addr);
            System.out.println(Util.readNextInstruction(_decoder, scanner));
        } else {
            System.out.println(String.format(ERROR_FORMAT, input));
        }
    }

    private void handleBreakpoint(String input) {
        if (input.charAt(1) != ' ') {
            System.out.println(String.format(ERROR_FORMAT, input));
            return;
        }

        Short addr = tryParseAddr(input.substring(2));
        if (addr == null)
            return;

        _breakpoints.put(++_maxBreakpointSeen, addr);
        System.out.println(String.format("Breakpoint %d set at %s",
                _maxBreakpointSeen, Util.shortToHexString(addr)));
    }

    private void handleDeleteBreakpoint(String input) {
        if (input.charAt(1) != ' ') {
            System.out.println(String.format(ERROR_FORMAT, input));
            return;
        }

        Integer id = tryParse(input.substring(2), false, 0, _maxBreakpointSeen);
        if (id == null)
            return;

        if (_breakpoints.containsKey(id))
            _breakpoints.remove(id);
    }

    private void printRegisters(EmulatorState state) {
        System.out.println("Registers:");
        System.out.println(String.format("\t\tAF: %s\t\tSP: %s",
                Util.shortToHexString(Register16.AF.get(state)),
                Util.shortToHexString(Register16.SP.get(state))));
        System.out.println(String.format("\t\tBC: %s\t\tPC: %s",
                Util.shortToHexString(Register16.BC.get(state)),
                Util.shortToHexString(Register16.PC.get(state))));
        System.out.println(String.format("\t\tDE: %s",
                Util.shortToHexString(Register16.DE.get(state))));
        System.out.println(String.format("\t\tHL: %s",
                Util.shortToHexString(Register16.HL.get(state))));
        System.out.println(String.format("\t\tZ:%d  N:%d  H:%d  C:%d",
                state.registerState.flags.getZ(),
                state.registerState.flags.getN(),
                state.registerState.flags.getH(),
                state.registerState.flags.getC()));
    }

    private void printNextInstructions(EmulatorState state) {
        int address = Register16.PC.get(state) & 0xFFFF;

        ByteScanner scanner = new ByteScanner(new MemoryByteSource(state.memory));
        scanner.seek(address);

        System.out.println();
        for (int i = 0; i < NUM_INSTRUCTIONS_PRINTED; i++)
            System.out.println("\t" + Util.readNextInstruction(_decoder, scanner));
    }

    private void printState(EmulatorState state) {
        printRegisters(state);
        System.out.println();

        printNextInstructions(state);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Path?");
        new RomDebugger(reader, reader.readLine()).run();
    }
}
