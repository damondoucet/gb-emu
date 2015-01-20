package cpu.disassembler.decoder_tests;

import cpu.disassembler.instruction_args.DereferencedRegisterShort;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.ShortConst;
import cpu.disassembler.instruction_args.ValueContainer;
import cpu.disassembler.instructions.PCControlInstructions;
import org.junit.Test;

/**
 * Test that all PC Control Instructions are decoded correctly.
 */
public class PCControlDecoderTests extends DecoderTests {
    @Test
    public void testNopDecoded() {
        testDecode(new byte[] { (byte)0x00 }, new PCControlInstructions.NopInstruction(), 4);
    }

    @Test
    public void testJrDecoded() {
        testDecode(new byte[] { (byte)0x18, (byte)0x12 }, new PCControlInstructions.JrInstruction(
                PCControlInstructions.JumpFlag.None, false, (byte)0x12), 12);
        testDecode(new byte[] { (byte)0x20, (byte)0x12 }, new PCControlInstructions.JrInstruction(
                PCControlInstructions.JumpFlag.Z, true, (byte)0x12), 8);
        testDecode(new byte[] { (byte)0x30, (byte)0x12 }, new PCControlInstructions.JrInstruction(
                PCControlInstructions.JumpFlag.C, true, (byte)0x12), 8);
        testDecode(new byte[] { (byte)0x28, (byte)0x12 }, new PCControlInstructions.JrInstruction(
                PCControlInstructions.JumpFlag.Z, false, (byte)0x12), 8);
        testDecode(new byte[] { (byte)0x38, (byte)0x12 }, new PCControlInstructions.JrInstruction(
                PCControlInstructions.JumpFlag.C, false, (byte)0x12), 8);
    }

    @Test
    public void testJpDecoded() {
        ValueContainer<Short> addr = new ShortConst((short)0x1234);
        testDecode(new byte[] { (byte)0xC3, (byte)0x34, (byte)0x12 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.None, false, addr), 16);
        testDecode(new byte[] { (byte)0xC2, (byte)0x34, (byte)0x12 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.Z, true, addr), 12);
        testDecode(new byte[] { (byte)0xD2, (byte)0x34, (byte)0x12 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.C, true, addr), 12);
        testDecode(new byte[] { (byte)0xCA, (byte)0x34, (byte)0x12 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.Z, false, addr), 12);
        testDecode(new byte[] { (byte)0xDA, (byte)0x34, (byte)0x12 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.C, false, addr), 12);

        addr = new DereferencedRegisterShort(Register16.HL);
        testDecode(new byte[] { (byte)0xE9 }, new PCControlInstructions.JpInstruction(
                PCControlInstructions.JumpFlag.None, false, addr), 4);
    }

    @Test
    public void testCallDecoded() {
        ValueContainer<Short> addr = new ShortConst((short)0x1234);
        testDecode(new byte[] { (byte)0xCD, (byte)0x34, (byte)0x12 }, new PCControlInstructions.CallInstruction(
                PCControlInstructions.JumpFlag.None, false, addr), 24);
        testDecode(new byte[] { (byte)0xC4, (byte)0x34, (byte)0x12 }, new PCControlInstructions.CallInstruction(
                PCControlInstructions.JumpFlag.Z, true, addr), 12);
        testDecode(new byte[] { (byte)0xD4, (byte)0x34, (byte)0x12 }, new PCControlInstructions.CallInstruction(
                PCControlInstructions.JumpFlag.C, true, addr), 12);
        testDecode(new byte[] { (byte)0xCC, (byte)0x34, (byte)0x12 }, new PCControlInstructions.CallInstruction(
                PCControlInstructions.JumpFlag.Z, false, addr), 12);
        testDecode(new byte[] { (byte)0xDC, (byte)0x34, (byte)0x12 }, new PCControlInstructions.CallInstruction(
                PCControlInstructions.JumpFlag.C, false, addr), 12);
    }

    @Test
    public void testRstDecoded() {
        testDecode(new byte[] { (byte)0xC7 },
                new PCControlInstructions.RstInstruction((byte)0x00), 16);
        testDecode(new byte[] { (byte)0xCF },
                new PCControlInstructions.RstInstruction((byte)0x08), 16);
        testDecode(new byte[] { (byte)0xD7 },
                new PCControlInstructions.RstInstruction((byte)0x10), 16);
        testDecode(new byte[] { (byte)0xDF },
                new PCControlInstructions.RstInstruction((byte)0x18), 16);
        testDecode(new byte[] { (byte)0xE7 },
                new PCControlInstructions.RstInstruction((byte)0x20), 16);
        testDecode(new byte[] { (byte)0xEF },
                new PCControlInstructions.RstInstruction((byte)0x28), 16);
        testDecode(new byte[] { (byte)0xF7 },
                new PCControlInstructions.RstInstruction((byte)0x30), 16);
        testDecode(new byte[] { (byte)0xFF },
                new PCControlInstructions.RstInstruction((byte)0x38), 16);
    }

    @Test
    public void testRetDecoded() {
        testDecode(new byte[] { (byte)0xC9 }, new PCControlInstructions.RetInstruction(
                PCControlInstructions.JumpFlag.None, false), 16);
        testDecode(new byte[] { (byte)0xD9 }, new PCControlInstructions.RetiInstruction(), 16);
        testDecode(new byte[] { (byte)0xC0 }, new PCControlInstructions.RetInstruction(
                PCControlInstructions.JumpFlag.Z, true), 8);
        testDecode(new byte[] { (byte)0xD0 }, new PCControlInstructions.RetInstruction(
                PCControlInstructions.JumpFlag.C, true), 8);
        testDecode(new byte[] { (byte)0xC8 }, new PCControlInstructions.RetInstruction(
                PCControlInstructions.JumpFlag.Z, false), 8);
        testDecode(new byte[] { (byte)0xD8 }, new PCControlInstructions.RetInstruction(
                PCControlInstructions.JumpFlag.C, false), 8);
    }
}
