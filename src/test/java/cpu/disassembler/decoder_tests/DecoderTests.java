package cpu.disassembler.decoder_tests;

import cpu.disassembler.Instruction;
import cpu.disassembler.InstructionDecoder;
import cpu.disassembler.RootInstructionDecoder;
import cpu.disassembler.instruction_args.DereferencedRegisterByte;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instruction_args.SettableValueContainer;
import junit.framework.Assert;
import util.ByteScanner;

import java.util.Arrays;
import java.util.List;

/**
 * Tests that the decoder correctly decodes all instructions as well as number
 * of cycles each instruction should take.
 *
 * See http://www.pastraiser.com/cpu/gameboy/gameboy_opcodes.html
 */
public class DecoderTests {
    protected final static List<SettableValueContainer<Byte>> arg8s =
            Arrays.asList(Register8.B, Register8.C, Register8.D, Register8.E,
                    Register8.H, Register8.L,
                    new DereferencedRegisterByte(Register16.HL), Register8.A
            );

    protected final static List<Register16> arg16sWithoutAF =
            Arrays.asList(Register16.BC, Register16.DE, Register16.HL, Register16.SP);

    protected final static List<Register16> arg16sWithAF =
            Arrays.asList(Register16.BC, Register16.DE, Register16.HL, Register16.AF);

    protected void testDecode(
            byte[] bytes, Instruction expectedInstruction, int expectedMinCycles) {
        ByteScanner scanner = new ByteScanner(bytes);

        InstructionDecoder decoder = new RootInstructionDecoder();

        // getMinimumCycles() peeks, whereas decodeNext consumes. Thus, it's
        // important to get the cycles before decoding.
        int actualMinCycles = decoder.getMinimumCycles(scanner);
        Instruction actualInstruction = decoder.decodeNext(scanner);

        Assert.assertEquals(expectedMinCycles, actualMinCycles);
        Assert.assertEquals(expectedInstruction, actualInstruction);

        // Test that we consumed exactly as many bytes as we had.
        Assert.assertEquals(bytes.length, scanner.getIndex());
    }
}
