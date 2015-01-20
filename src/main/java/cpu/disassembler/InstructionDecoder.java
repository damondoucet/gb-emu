package cpu.disassembler;

import util.ByteScanner;
import util.Util;

import static com.google.common.base.Preconditions.*;

import java.util.List;

/**
 * An InstructionDecoder takes a list of instruction factories that define the
 * circumstances in which they can parse the next instruction (based on the
 * next byte in the stream). The decoder provides a single operation, which is
 * to decode the next instruction in the stream and update the position of the
 * scanner.
 *
 * Two classes extend this: RootInstructionDecoder and MultiByteInstructionDecoder.
 */
public class InstructionDecoder {
    private final List<InstructionFactory> _instructionFactories;

    protected InstructionDecoder(List<InstructionFactory> instructionFactories) {
        _instructionFactories = instructionFactories;
    }

    // Find exactly one factory that can parse the given byte. If more than one
    // or none can, throw an exception.
    private InstructionFactory findFactory(byte nextByte) {
        InstructionFactory ret = null;

        for (InstructionFactory factory : _instructionFactories) {
            if (factory.canParse(nextByte)) {
                if (ret != null) {
                    String error = String.format("Already found an instruction" +
                            "factory that can parse byte %s. First factory: %s;" +
                            " second: %s",
                            Util.byteToHexString(nextByte),
                            ret.getClass().getName(), factory.getClass().getName());
                    throw new IllegalStateException(error);
                }

                ret = factory;
            }
        }

        checkState(ret != null, "Could not find a factory that could parse %s",
                Util.byteToHexString(nextByte));
        return ret;
    }

    /*
     * Returns the minimum number of cycles for the next position and does NOT
     * advance the position of the stream. See
     * InstructionFactory.getMinimumCycles()
     */
    public int getMinimumCycles(ByteScanner scanner) {
        return findFactory(scanner.peek()).getMinimumCycles(scanner);
    }

    /*
     * Decodes instructions on-demand. We do not cache decoded instructions,
     * nor do we decode all instructions on initialization. This is much
     * simpler (and thus much easier to get correct), and likely, due to the
     * small instruction set, will not incur a noticeable performance penalty.
     */
    public Instruction decodeNext(ByteScanner scanner) {
        return findFactory(scanner.peek()).decodeInstruction(scanner);
    }
}
