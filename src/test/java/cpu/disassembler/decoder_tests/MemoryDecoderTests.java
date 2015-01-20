package cpu.disassembler.decoder_tests;

import cpu.disassembler.instruction_args.*;
import cpu.disassembler.instructions.MemoryInstructions;
import org.junit.Test;

/**
 * Test that all Memory Instructions are decoded correctly.
 */
public class MemoryDecoderTests extends DecoderTests {
    @Test
    public void testPushDecoded() {
        for (int reg = 0; reg < 4; reg++) {
            testDecode(new byte[] { (byte)(0xC5 + reg * 16) },
                    new MemoryInstructions.PushInstruction(arg16sWithAF.get(reg)),
                    16);
        }
    }

    @Test
    public void testPopDecoded() {
        for (int reg = 0; reg < 4; reg++) {
            testDecode(new byte[] { (byte)(0xC1 + reg * 16) },
                    new MemoryInstructions.PopInstruction(arg16sWithAF.get(reg)),
                    12);
        }
    }

    @Test
    public void testLdSpDecoded() {
        testDecode(new byte[] { (byte)0x08, (byte)0x34, (byte)0x12 },
                new MemoryInstructions.Ld16Instruction(
                        new ShortPointer((short)0x1234),
                        Register16.SP),
                20);

        testDecode(new byte[] { (byte)0xF8, (byte)0x12 },
                new MemoryInstructions.LdSpToHLInstruction((byte)0x12), 12);
        testDecode(new byte[] { (byte)0xF9 },
                new MemoryInstructions.LdHLToSpInstruction(), 8);
    }

    @Test
    public void testLdr16ConstDecoded() {
        for (int reg = 0; reg < 4; reg++) {
            testDecode(new byte[] { (byte)(0x01 + 16 * reg), (byte)0x34, (byte)0x12 },
                    new MemoryInstructions.Ld16Instruction(arg16sWithoutAF.get(reg),
                            new ShortConst((short)0x1234)), 12);
        }
    }

    @Test
    public void testLdDecodedi() {
        testDecode(new byte[] { (byte)0x22 }, MemoryInstructions.LdiInstruction.ldiToHL(), 8);
        testDecode(new byte[] { (byte)0x2A }, MemoryInstructions.LdiInstruction.ldiFromHL(), 8);
    }

    @Test
    public void testLddDecoded() {
        testDecode(new byte[] { (byte)0x32 }, MemoryInstructions.LddInstruction.lddToHL(), 8);
        testDecode(new byte[] { (byte)0x3A }, MemoryInstructions.LddInstruction.lddFromHL(), 8);
    }

    @Test
    public void testLdAPointerDecoded() {
        testDecode(new byte[] { (byte)0x02 },
                new MemoryInstructions.Ld8Instruction(
                        new DereferencedRegisterByte(Register16.BC), Register8.A), 8);
        testDecode(new byte[] { (byte)0x0A },
                new MemoryInstructions.Ld8Instruction(
                        Register8.A, new DereferencedRegisterByte(Register16.BC)), 8);
        testDecode(new byte[] { (byte)0x12 },
                new MemoryInstructions.Ld8Instruction(
                        new DereferencedRegisterByte(Register16.DE), Register8.A), 8);
        testDecode(new byte[] { (byte)0x1A },
                new MemoryInstructions.Ld8Instruction(
                        Register8.A, new DereferencedRegisterByte(Register16.DE)), 8);

        testDecode(new byte[] { (byte)0xEA, (byte)0x34, (byte)0x12 },
                new MemoryInstructions.Ld8Instruction(
                        new BytePointer((short)0x1234), Register8.A), 16);
        testDecode(new byte[] { (byte)0xFA, (byte)0x34, (byte)0x12 },
                new MemoryInstructions.Ld8Instruction(
                        Register8.A, new BytePointer((short)0x1234)), 16);
    }

    @Test
    public void testLdhDecoded() {
        testDecode(new byte[] { (byte)0xE0, (byte)0x12 },
                new MemoryInstructions.Ld8Instruction(
                        new BytePointer((short)0xFF12), Register8.A), 12);
        testDecode(new byte[] { (byte)0xF0, (byte)0x12 },
                new MemoryInstructions.Ld8Instruction(
                        Register8.A, new BytePointer((short)0xFF12)), 12);
    }

    @Test
    public void testLdIoDecoded() {
        testDecode(new byte[] { (byte)0xE2 },
                MemoryInstructions.LdIoPortInstruction.LdToIoPort(), 8);
        testDecode(new byte[] { (byte)0xF2 },
                MemoryInstructions.LdIoPortInstruction.LdFromIoPort(), 8);
    }

    @Test
    public void testLdr8ConstDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            testDecode(new byte[] { (byte)(0x06 + reg * 8), 0x12 },
                    new MemoryInstructions.Ld8Instruction(arg8s.get(reg), new ByteConst((byte)0x12)),
                    reg == 6 ? 12 : 8);
        }
    }

    @Test
    public void testLdr8r8Decoded() {
        for (int dest = 0; dest < 8; dest++) {
            for (int src = 0; src < 8; src++) {
                // 0x76 is HALT, not LD (HL), (HL)
                if (dest == 6 && src == 6)
                    continue;

                byte command = (byte)(0x40 + dest * 8 + src);
                testDecode(new byte[] { command },
                        new MemoryInstructions.Ld8Instruction(arg8s.get(dest), arg8s.get(src)),
                        src == 6 || dest == 6 ? 8 : 4);
            }
        }
    }
}
