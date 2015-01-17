package cpu.disassembler;

import cpu.EmulatorState;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instructions.BitwiseInstructions;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests bitwise instructions:
 *      RLC, RRC, RL, RR, SLA, SRA, SRL, SWAP, BIT, RES, SET,
 *      AND, OR, XOR, CPL
 *
 *  These tests assume that the registers all work so it only tests using a
 *  single register (A).
 */
public class BitwiseInstructionTests {
    private void testInstructionValue(
            Instruction instr,
            byte value,
            byte expectedValue) {
        testInstructionValueCarry(instr, value, 0, expectedValue, 0);
    }

    // The instruction should be configured to use the A register.
    // Given a value, tests expected value and expected carry flag.
    private void testInstructionValueCarry(
            Instruction instr,
            byte value,
            int carry,
            byte expectedValue,
            int expectedCarry) {
        EmulatorState state = new EmulatorState();
        Register8.A.set(state, value);
        state.registerState.flags.setC(carry);

        instr.execute(state);

        Assert.assertEquals(expectedValue, (byte) Register8.A.get(state));
        Assert.assertEquals(expectedCarry, state.registerState.flags.getC());

        int isZero = expectedValue == 0 ? 1 : 0;
        Assert.assertEquals(isZero, state.registerState.flags.getZ());
    }

    @Test
    public void testRlcInstruction() {
        Instruction instr = new BitwiseInstructions.RlcInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte) 0x00, 0, (byte) 0x00, 0);
        testInstructionValueCarry(instr, (byte) 0x80, 0, (byte) 0x01, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
    }

    @Test
    public void testRrcInstruction() {
        Instruction instr = new BitwiseInstructions.RrcInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x01, 0, (byte)0x80, 1);
        testInstructionValueCarry(instr, (byte)0x80, 0, (byte)0x40, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
    }

    @Test
    public void testRlInstruction() {
        Instruction instr = new BitwiseInstructions.RlInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x00, 1, (byte)0x01, 0);
        testInstructionValueCarry(instr, (byte)0x80, 0, (byte)0x00, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 1, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFE, 1);
    }

    @Test
    public void testRrInstruction() {
        Instruction instr = new BitwiseInstructions.RrInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x00, 1, (byte)0x80, 0);
        testInstructionValueCarry(instr, (byte)0x00, 0, (byte)0x00, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 1, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0x7F, 1);
    }

    @Test
    public void testSlaInstruction() {
        Instruction instr = new BitwiseInstructions.SlaInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte) 0x01, 0, (byte) 0x02, 0);
        testInstructionValueCarry(instr, (byte) 0x80, 0, (byte) 0x00, 1);
        testInstructionValueCarry(instr, (byte) 0xFF, 0, (byte) 0xFE, 1);
    }

    @Test
    public void testSraInstruction() {
        Instruction instr = new BitwiseInstructions.SraInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0x10, 0, (byte)0x08, 0);
        testInstructionValueCarry(instr, (byte)0x05, 0, (byte)0x02, 1);
    }

    @Test
    public void testSrlInstruction() {
        Instruction instr = new BitwiseInstructions.SrlInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x01, 0, (byte)0x00, 1);
        testInstructionValueCarry(instr, (byte)0x10, 0, (byte)0x08, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0x7F, 1);
    }

    @Test
    public void testSwapInstruction() {
        Instruction instr = new BitwiseInstructions.SwapInstruction(Register8.A);
        testInstructionValue(instr, (byte)0xEF, (byte)0xFE);
        testInstructionValue(instr, (byte)0x21, (byte)0x12);
        testInstructionValue(instr, (byte)0x00, (byte)0x00);
    }

    // Expects all registers to have value 0. Leaves all registers with value
    // 0xFF
    private void testSetAndBitInstructions(EmulatorState state) {
        byte expectedValue = 0;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            expectedValue = (byte)(expectedValue| (1 << bitIndex));

            // Test setting the bit.
            new BitwiseInstructions.SetInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(expectedValue, (byte) Register8.A.get(state));

            // Test checking the bit.
            new BitwiseInstructions.BitInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(0, state.registerState.flags.getZ());
            Assert.assertEquals(0, state.registerState.flags.getN());
            Assert.assertEquals(1, state.registerState.flags.getH());
        }
    }

    // Expects all registers to have value 0xFF. Leaves all registers with
    // value 0.
    private void testResetAndBitInstructions(EmulatorState state) {
        byte expectedValue = (byte)0xFF;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            expectedValue = (byte)(expectedValue & ~(1 << bitIndex));

            // Test resetting the bit.
            new BitwiseInstructions.ResInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(expectedValue, (byte)Register8.A.get(state));

            // Test checking the bit.
            new BitwiseInstructions.BitInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(1, state.registerState.flags.getZ());
            Assert.assertEquals(0, state.registerState.flags.getN());
            Assert.assertEquals(1, state.registerState.flags.getH());
        }
    }

    @Test
    public void testSetResetInstructions() {
        EmulatorState state = new EmulatorState();
        testSetAndBitInstructions(state);
        testResetAndBitInstructions(state);
    }
}
