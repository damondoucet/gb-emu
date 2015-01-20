package cpu.disassembler.decoder_tests;

import cpu.disassembler.instruction_args.*;
import cpu.disassembler.instructions.BitwiseInstructions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Test that all Bitwise Instructions are decoded correctly.
 */
public class BitwiseDecoderTests extends DecoderTests {
    @Test
    public void testBasicBitwiseInstrsDecoded() {
        testDecode(new byte[] { (byte)0x07 }, new BitwiseInstructions.RlcaInstruction(), 4);
        testDecode(new byte[] { (byte)0x0F }, new BitwiseInstructions.RrcaInstruction(), 4);
        testDecode(new byte[] { (byte)0x17 }, new BitwiseInstructions.RlaInstruction(), 4);
        testDecode(new byte[] { (byte)0x1F }, new BitwiseInstructions.RraInstruction(), 4);
        testDecode(new byte[] { (byte)0x2F }, new BitwiseInstructions.CplInstruction(), 4);
    }

    @Test
    public void testRlcDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)reg;
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.RlcInstruction(arg), cycles);
        }
    }

    @Test
    public void testRrcDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x08 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.RrcInstruction(arg), cycles);
        }
    }

    @Test
    public void testRlDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x10 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.RlInstruction(arg), cycles);
        }
    }

    @Test
    public void testRrDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x18 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.RrInstruction(arg), cycles);
        }
    }

    @Test
    public void testSlaDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x20 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.SlaInstruction(arg), cycles);
        }
    }

    @Test
    public void testSraDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x28 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.SraInstruction(arg), cycles);
        }
    }

    @Test
    public void testSwapDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x30 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.SwapInstruction(arg), cycles);
        }
    }

    @Test
    public void testSrlDecoded() {
        for (int reg = 0; reg < 8; reg++) {
            SettableValueContainer<Byte> arg = arg8s.get(reg);

            byte instr = (byte)(0x38 + reg);
            byte[] bytes = new byte[] { (byte)0xCB, instr };

            // reg == 6 -> (HL); requires a memory access
            int cycles = reg == 6 ? 16 : 8;

            testDecode(bytes, new BitwiseInstructions.SrlInstruction(arg), cycles);
        }
    }

    @Test
    public void testBitDecoded() {
        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            for (int reg = 0; reg < 8; reg++) {
                SettableValueContainer<Byte> arg = arg8s.get(reg);

                byte instr = (byte)(0x40 + bitIndex * 8 + reg);
                byte[] bytes = new byte[] { (byte)0xCB, instr };

                // reg == 6 -> (HL); requires a memory access
                int cycles = reg == 6 ? 16 : 8;

                testDecode(bytes, new BitwiseInstructions.BitInstruction(bitIndex, arg), cycles);
            }
        }
    }

    @Test
    public void testResDecoded() {
        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            for (int reg = 0; reg < 8; reg++) {
                SettableValueContainer<Byte> arg = arg8s.get(reg);

                byte instr = (byte)(0x80 + bitIndex * 8 + reg);
                byte[] bytes = new byte[] { (byte)0xCB, instr };

                // reg == 6 -> (HL)
                int cycles = reg == 6 ? 16 : 8;

                testDecode(bytes, new BitwiseInstructions.ResInstruction(bitIndex, arg), cycles);
            }
        }
    }

    @Test
    public void testSetDecoded() {
        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            for (int reg = 0; reg < 8; reg++) {
                SettableValueContainer<Byte> arg = arg8s.get(reg);

                byte instr = (byte)(0xC0 + bitIndex * 8 + reg);
                byte[] bytes = new byte[] { (byte)0xCB, instr };

                // reg == 6 -> (HL)
                int cycles = reg == 6 ? 16 : 8;

                testDecode(bytes, new BitwiseInstructions.SetInstruction(bitIndex, arg), cycles);
            }
        }
    }

    @Test
    public void testAndDecoded() {
        for (int i = 0; i < 8; i++)
            testDecode(new byte[] { (byte)(0xA0 + i) },
                    new BitwiseInstructions.AndInstruction(arg8s.get(i)),
                    i == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xE6, (byte)0x12 },
                new BitwiseInstructions.AndInstruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testXorDecoded() {
        for (int i = 0; i < 8; i++)
            testDecode(new byte[] { (byte)(0xA8 + i) },
                    new BitwiseInstructions.XorInstruction(arg8s.get(i)),
                    i == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xEE, (byte)0x12 },
                new BitwiseInstructions.XorInstruction(new ByteConst((byte)0x12)), 8);
    }

    @Test
    public void testOrDecoded() {
        for (int i = 0; i < 8; i++)
            testDecode(new byte[] { (byte)(0xB0 + i) },
                    new BitwiseInstructions.OrInstruction(arg8s.get(i)),
                    i == 6 ? 8 : 4);

        testDecode(new byte[] { (byte)0xF6, (byte)0x12 },
                new BitwiseInstructions.OrInstruction(new ByteConst((byte)0x12)), 8);
    }
}
