package util;

import cpu.EmulatorState;
import cpu.disassembler.InstructionDecoder;
import cpu.disassembler.RootInstructionDecoder;
import cpu.disassembler.instruction_args.Register16;
import memory.MemoryByteSource;

/**
 * Utilities for dumping the state of a ROM or emulator.
 */
public class DumpUtil {
    // Create a String of the bytes between originalIndex and the scanner's
    // current position.
    private static String getByteString(ByteScanner scanner, int originalIndex) {
        int finalIndex = scanner.getIndex();

        scanner.seek(originalIndex);
        String ret = "";

        while (scanner.getIndex() < finalIndex)
            ret += Util.byteToHexStringWithoutPrefix(scanner.readByte()) + " ";

        return ret;
    }

    private static String readInstruction(InstructionDecoder decoder, ByteScanner scanner) {
        try {
            return decoder.decodeNext(scanner).toString();
        } catch (Exception e) {
            return "DB " + Util.byteToHexString(scanner.readByte());
        }
    }

    public static String readNextInstruction(InstructionDecoder decoder, ByteScanner scanner) {
        int start = scanner.getIndex();

        String address = Util.shortToHexString((short)start);
        String instr = readInstruction(decoder, scanner);
        String bytes = getByteString(scanner, start);

        // The byte string will end with an extra space
        return String.format("%s - %s- %s", address, bytes, instr);
    }

    public static void printRegisters(EmulatorState state) {
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

    private final static int NUM_INSTRUCTIONS_PRINTED = 5;
    public static void printNextInstructions(EmulatorState state, InstructionDecoder decoder) {
        int address = Register16.PC.get(state) & 0xFFFF;

        ByteScanner scanner = new ByteScanner(new MemoryByteSource(state.memory));
        scanner.seek(address);

        System.out.println();
        for (int i = 0; i < NUM_INSTRUCTIONS_PRINTED; i++)
            System.out.println("\t" + DumpUtil.readNextInstruction(decoder, scanner));
    }

    public static void printNextInstructions(EmulatorState state) {
        printNextInstructions(state, new RootInstructionDecoder());
    }

    public static void printEmulatorState(EmulatorState state) {
        printRegisters(state);
        System.out.println();

        printNextInstructions(state);
    }
}
