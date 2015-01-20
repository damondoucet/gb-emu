package cpu.disassembler.decoder_tests;

import cpu.disassembler.instruction_args.*;
import cpu.disassembler.instructions.ArithmeticInstructions;
import org.junit.Test;

/**
 * Test that all Arithmetic Instructions are decoded correctly.
 */
public class ArithmeticDecoderTests extends DecoderTests {
    @Test
    public void testSimpleArithInstrsDecoded() {
        testDecode(new byte[] { (byte)0x27 }, new ArithmeticInstructions.DaaInstruction(), 4);
        testDecode(new byte[] { (byte)0x37 }, new ArithmeticInstructions.ScfInstruction(), 4);
        testDecode(new byte[] { (byte)0x3F }, new ArithmeticInstructions.CcfInstruction(), 4);
    }

    @Test
    public void testAdd16Decoded() {
        for (int reg = 0; reg < 4; reg++) {
            testDecode(new byte[] { (byte)(0x09 + reg * 16) },
                    new ArithmeticInstructions.Add16Instruction(Register16.HL, arg16sWithoutAF.get(reg)),
                    8);
        }

        testDecode(new byte[] { (byte)0xE8, (byte)0x12 },
                new ArithmeticInstructions.Add16Instruction(Register16.SP, new ShortConst((byte)0x12)),
                16);
    }

    @Test
    public void testAdd8Decoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(reg + 0x80) },
                    new ArithmeticInstructions.Add8Instruction(arg8s.get(reg)),
                    reg == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xC6, (byte)0x12 },
                new ArithmeticInstructions.Add8Instruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testAdcDecoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(reg + 0x88) },
                    new ArithmeticInstructions.AdcInstruction(arg8s.get(reg)),
                    reg == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xCE, (byte)0x12 },
                new ArithmeticInstructions.AdcInstruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testSubDecoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(reg + 0x90) },
                    new ArithmeticInstructions.SubInstruction(arg8s.get(reg)),
                    reg == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xD6, (byte)0x12 },
                new ArithmeticInstructions.SubInstruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testSbcDecoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(reg + 0x98) },
                    new ArithmeticInstructions.SbcInstruction(arg8s.get(reg)),
                    reg == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xDE, (byte)0x12 },
                new ArithmeticInstructions.SbcInstruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testInc8Decoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(8 * reg + 0x04) },
                    new ArithmeticInstructions.Inc8Instruction(arg8s.get(reg)),
                    reg == 6 ? 12 : 4);
    }

    @Test
    public void testDec8Decoded() {
        for (int reg = 0; reg < 8; reg++)
            testDecode(new byte[] { (byte)(8 * reg + 0x05) },
                    new ArithmeticInstructions.Dec8Instruction(arg8s.get(reg)),
                    reg == 6 ? 12 : 4);
    }

    @Test
    public void testInc16Decoded() {
        for (int i = 0; i < 4; i++)
            testDecode(new byte[] { (byte)(16 * i + 0x03) },
                    new ArithmeticInstructions.Inc16Instruction(arg16sWithoutAF.get(i)), 8);
    }

    @Test
    public void testDec16Decoded() {
        for (int i = 0; i < 4; i++)
            testDecode(new byte[] { (byte)(16 * i + 0x0B) },
                    new ArithmeticInstructions.Dec16Instruction(arg16sWithoutAF.get(i)), 8);
    }

    @Test
    public void testCpDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            byte command = (byte)(0xB8 + reg);
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            // reg == 6 -> (HL), which is a memory reference
            int cycles = reg == 6 ? 8 : 4;

            testDecode(new byte[] { command }, new ArithmeticInstructions.CpInstruction(arg), cycles);
        }

        testDecode(new byte[] { (byte)0xFE, (byte)0x12 },
                new ArithmeticInstructions.CpInstruction(new ByteConst((byte)0x12)), 8);
    }
}
