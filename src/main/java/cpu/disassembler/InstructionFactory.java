package cpu.disassembler;

import util.ByteScanner;
import util.Util;

import static com.google.common.base.Preconditions.*;

/**
 * Each factory represents a single type of instruction: either a single
 * instruction (e.g. ADD) or the special MultiByteInstructionFactory, which
 * represents all instructions with two-byte commands.
 */
public abstract class InstructionFactory {
    /*
     * Asserts that this factory can in fact parse the next instruction, and
     * then parses the instruction and updates the scanner's position.
     */
    public Instruction decodeInstruction(ByteScanner scanner) {
        byte nextByte = scanner.peek();

        checkArgument(canParse(nextByte), "%s is not a legal next byte for %s",
                Util.byteToHexString(nextByte), getClass().getName());

        return uncheckedDecodeInstruction(scanner);
    }

    /*
     * Returns whether this factory can parse an instruction when the next byte
     * in the stream is the parameter.
     *
     * This is used as a sanity check.
     */
    protected abstract boolean canParse(byte nextByte);

    /*
     * Returns the minimum number of cycles the instruction will execute for.
     * During execution, the CPU should add this number to the
     * getAdditionalCycles() method on the instruction returns from
     * decodeInstruction(scanner). (Conditional instructions may require more
     * cycles to execute, depending on whether the branch was taken or not).
     *
     * Note that this should not advance the position of the stream.
     */
    protected abstract int getMinimumCycles(ByteScanner scanner);

    /*
     * This method assumes that canParse(scanner.peek()) is true. Undefined
     * behavior occurs in the case that it is not.
     *
     * Parses the next instruction in the scanner and updates its position.
     */
    protected abstract Instruction uncheckedDecodeInstruction(ByteScanner scanner);
}
