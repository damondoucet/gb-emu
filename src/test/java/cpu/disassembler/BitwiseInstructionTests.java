package cpu.disassembler;

import cpu.CpuState;
import cpu.disassembler.instruction_args.Register8;
import junit.framework.Assert;
import org.junit.Test;

// TODO(ddoucet): add ZF tests
/**
 * Tests bitwise instructions:
 *      RLC, RRC, RL, RR, SLA, SRA, SRL, SWAP, BIT, RES, SET
 *
 *  These tests assume that the registers all work so it only tests using a
 *  single register (A).
 */
public class BitwiseInstructionTests {
    private final static Register8[] registers = new Register8[] {
        Register8.A, Register8.B, Register8.C, Register8.D, Register8.E,
        Register8.H, Register8.L
    };

    @Test
    public void JavaWhy() {
        // One would expect that for a given byte b, b & 0xFF is a no-op...
        // but apparently Java disagrees.
        byte b = (byte)-1;
        Assert.assertEquals((byte) (b >>> 1), -1);
        Assert.assertEquals((byte) ((b & 0xFF) >>> 1), 127);
    }

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
        CpuState state = new CpuState();
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
        Instruction instr = new Instructions.RlcInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte) 0x00, 0, (byte) 0x00, 0);
        testInstructionValueCarry(instr, (byte) 0x80, 0, (byte) 0x01, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
    }

    @Test
    public void testRrcInstruction() {
        Instruction instr = new Instructions.RrcInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x01, 0, (byte)0x80, 1);
        testInstructionValueCarry(instr, (byte)0x80, 0, (byte)0x40, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
    }

    @Test
    public void testRlInstruction() {
        Instruction instr = new Instructions.RlInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x00, 1, (byte)0x01, 0);
        testInstructionValueCarry(instr, (byte)0x80, 0, (byte)0x00, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 1, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFE, 1);
    }

    @Test
    public void testRrInstruction() {
        Instruction instr = new Instructions.RrInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x00, 1, (byte)0x80, 0);
        testInstructionValueCarry(instr, (byte)0x00, 0, (byte)0x00, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 1, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0x7F, 1);
    }

    @Test
    public void testSlaInstruction() {
        Instruction instr = new Instructions.SlaInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte) 0x01, 0, (byte) 0x02, 0);
        testInstructionValueCarry(instr, (byte) 0x80, 0, (byte) 0x00, 1);
        testInstructionValueCarry(instr, (byte) 0xFF, 0, (byte) 0xFE, 1);
    }

    @Test
    public void testSraInstruction() {
        Instruction instr = new Instructions.SraInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0xFF, 1);
        testInstructionValueCarry(instr, (byte)0x10, 0, (byte)0x08, 0);
        testInstructionValueCarry(instr, (byte)0x05, 0, (byte)0x02, 1);
    }

    @Test
    public void testSrlInstruction() {
        Instruction instr = new Instructions.SrlInstruction(Register8.A);
        testInstructionValueCarry(instr, (byte)0x01, 0, (byte)0x00, 1);
        testInstructionValueCarry(instr, (byte)0x10, 0, (byte)0x08, 0);
        testInstructionValueCarry(instr, (byte)0xFF, 0, (byte)0x7F, 1);
    }

    @Test
    public void testSwapInstruction() {
        Instruction instr = new Instructions.SwapInstruction(Register8.A);
        testInstructionValue(instr, (byte)0xEF, (byte)0xFE);
        testInstructionValue(instr, (byte)0x21, (byte)0x12);
        testInstructionValue(instr, (byte)0x00, (byte)0x00);
    }

    // Expects all registers to have value 0. Leaves all registers with value
    // 0xFF
    private void testSetAndBitInstructions(CpuState state) {
        byte expectedValue = 0;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            expectedValue = (byte)(expectedValue| (1 << bitIndex));

            // Test setting the bit.
            new Instructions.SetInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(expectedValue, (byte) Register8.A.get(state));

            // Test checking the bit.
            new Instructions.BitInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(0, state.registerState.flags.getZ());
            Assert.assertEquals(0, state.registerState.flags.getN());
            Assert.assertEquals(1, state.registerState.flags.getH());
        }
    }

    // Expects all registers to have value 0xFF. Leaves all registers with
    // value 0.
    private void testResetAndBitInstructions(CpuState state) {
        byte expectedValue = (byte)0xFF;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            expectedValue = (byte)(expectedValue & ~(1 << bitIndex));

            // Test resetting the bit.
            new Instructions.ResInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(expectedValue, (byte)Register8.A.get(state));

            // Test checking the bit.
            new Instructions.BitInstruction(bitIndex, Register8.A).execute(state);
            Assert.assertEquals(1, state.registerState.flags.getZ());
            Assert.assertEquals(0, state.registerState.flags.getN());
            Assert.assertEquals(1, state.registerState.flags.getH());
        }
    }

    @Test
    public void testSetResetInstructions() {
        CpuState state = new CpuState();
        testSetAndBitInstructions(state);
        testResetAndBitInstructions(state);
    }
}
