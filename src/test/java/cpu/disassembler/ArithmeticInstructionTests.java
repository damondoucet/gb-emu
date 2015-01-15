package cpu.disassembler;

import cpu.CpuState;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instructions.ArithmeticInstructions;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests arithmetic operations:
 *      ADD, ADC, SUB, SBC,
 *      INC, DEC
 *      CP, DAA
 */
public class ArithmeticInstructionTests {
    private void testFlags(CpuState state, int Z, int N, int H, int C) {
        Assert.assertEquals(C, state.registerState.flags.getC());
        Assert.assertEquals(H, state.registerState.flags.getH());
        Assert.assertEquals(N, state.registerState.flags.getN());
        Assert.assertEquals(Z, state.registerState.flags.getZ());
    }

    // Tests an 8-bit arithmetic instruction given two values, the expected
    // result, and the expected flags (Z flag is computable so not specified).
    // The instruction is assumed to use the B register.
    // We need carry here but not for the 16-bit instructions because this
    // method is also used to test ADC and SBC instructions (which don't have
    // 16-bit versions).
    private void testInstr8(
            Instruction instr,
            byte aValue,
            byte bValue,
            int carry,
            byte expectedA,
            int expectedCarry,
            int expectedHalfCarry,
            int expectedSubtract) {
        CpuState state = new CpuState();

        Register8.A.set(state, aValue);
        Register8.B.set(state, bValue);
        state.registerState.flags.setC(carry);

        instr.execute(state);

        Assert.assertEquals(expectedA, (byte)Register8.A.get(state));

        int isZero = expectedA == 0 ? 1 : 0;
        testFlags(state, isZero, expectedSubtract, expectedHalfCarry, expectedCarry);
    }

    // Same as above except for 16-bit instructions and the BC register.
    private void testInstr16(
            Instruction instr,
            short hlValue,
            short bcValue,
            short expectedHL,
            int expectedCarry,
            int expectedHalfCarry,
            int expectedSubtract) {
        CpuState state = new CpuState();

        Register16.HL.set(state, hlValue);
        Register16.BC.set(state, bcValue);

        instr.execute(state);

        Assert.assertEquals(expectedHL, (short)Register16.HL.get(state));

        int isZero = expectedHL == 0 ? 1 : 0;
        testFlags(state, isZero, expectedSubtract, expectedHalfCarry, expectedCarry);
    }

    @Test
    public void testAdd8() {
        Instruction instr = new ArithmeticInstructions.Add8Instruction(Register8.B);
        testInstr8(instr, (byte) 0, (byte) 0, 0, (byte) 0, 0, 0, 0);
        testInstr8(instr, (byte) 10, (byte) 7, 0, (byte) 17, 0, 1, 0);
        testInstr8(instr, (byte) 0xff, (byte) 1, 0, (byte) 0, 1, 1, 0);
        testInstr8(instr, (byte) 0xf0, (byte) 0x11, 0, (byte) 1, 1, 0, 0);
    }

    @Test
    public void testAdd16() {
        Instruction instr = new ArithmeticInstructions.Add16Instruction(Register16.BC);
        testInstr16(instr, (short) 0, (short) 0, (short) 0, 0, 0, 0);
        testInstr16(instr, (short) 10, (short) 7, (short) 17, 0, 0, 0);
        testInstr16(instr, (short) 2048, (short) 2048, (short) 4096, 0, 1, 0);
        testInstr16(instr, (short) 0xf000, (short) 0x1001, (short) 1, 1, 0, 0);
        testInstr16(instr, (short)0xffff, (short) 1, (short) 0, 1, 1, 0);
    }

    @Test
    public void testAdc() {
        // Run the add tests, both without the carry (should function as
        // normal), and with the carry, but one less in the second argument--
        // should still have the same results.
        Instruction instr = new ArithmeticInstructions.AdcInstruction(Register8.B);
        testInstr8(instr, (byte) 0, (byte) 0, 0, (byte) 0, 0, 0, 0);
        testInstr8(instr, (byte) 10, (byte) 7, 0, (byte) 17, 0, 1, 0);
        testInstr8(instr, (byte) 0xff, (byte) 1, 0, (byte) 0, 1, 1, 0);
        testInstr8(instr, (byte) 0xf0, (byte) 0x11, 0, (byte) 1, 1, 0, 0);

        testInstr8(instr, (byte) 0, (byte) 0xff, 1, (byte)0x00, 1, 1, 0);
        testInstr8(instr, (byte) 0xff, (byte) 0, 1, (byte)0x00, 1, 1, 0);
        testInstr8(instr, (byte) 10, (byte) 6, 1, (byte) 17, 0, 1, 0);
        testInstr8(instr, (byte) 0xff, (byte) 0, 1, (byte) 0, 1, 1, 0);
        testInstr8(instr, (byte) 0xf0, (byte) 0x10, 1, (byte) 1, 1, 0, 0);
    }

    @Test
    public void testSub8() {
        Instruction instr = new ArithmeticInstructions.Sub8Instruction(Register8.B);
        testInstr8(instr, (byte)0, (byte)0, 0, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0x10, (byte)0x10, 0, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0xe, (byte)0xf, 0, (byte)0xff, 1, 1, 1);
        testInstr8(instr, (byte)0xf, (byte)0x10, 0, (byte)0xff, 1, 0, 1);
        testInstr8(instr, (byte)0xe0, (byte)0xf0, 0, (byte)0xf0, 1, 0, 1);
        testInstr8(instr, (byte)0xf0, (byte)0x01, 0, (byte)0xef, 0, 1, 1);
    }

    @Test
    public void testSub16() {
        Instruction instr = new ArithmeticInstructions.Sub16Instruction(Register16.BC);
        testInstr16(instr, (short)0, (short)0, (short)0, 0, 0, 1);
        testInstr16(instr, (short)0x10, (short)0x10, (short)0, 0, 0, 1);
        testInstr16(instr, (short)0xe, (short)0xf, (short)0xffff, 1, 1, 1);
        testInstr16(instr, (short)0x0f00, (short)0x1000, (short)0xff00, 1, 0, 1);
        testInstr16(instr, (short)0xe000, (short)0xf000, (short)0xf000, 1, 0, 1);
        testInstr16(instr, (short)0xf000, (short)0x0100, (short)0xef00, 0, 1, 1);
        testInstr16(instr, (short)0x0100, (short)0x00ff, (short)0x0001, 0, 0, 1);
    }

    @Test
    public void testSbc() {
        Instruction instr = new ArithmeticInstructions.SbcInstruction(Register8.B);
        testInstr8(instr, (byte)0, (byte)0, 0, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0x10, (byte)0x10, 0, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0xe, (byte)0xf, 0, (byte)0xff, 1, 1, 1);
        testInstr8(instr, (byte)0xf, (byte)0x10, 0, (byte)0xff, 1, 0, 1);
        testInstr8(instr, (byte)0xe0, (byte)0xf0, 0, (byte)0xf0, 1, 0, 1);
        testInstr8(instr, (byte)0xf0, (byte)0x01, 0, (byte)0xef, 0, 1, 1);

        testInstr8(instr, (byte)1, (byte)0, 1, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0, (byte)0, 1, (byte)0xff, 1, 1, 1);
        testInstr8(instr, (byte)0x10, (byte)0x0f, 1, (byte)0, 0, 0, 1);
        testInstr8(instr, (byte)0xe, (byte)0x0e, 1, (byte)0xff, 1, 1, 1);
        testInstr8(instr, (byte)0xf, (byte)0x0f, 1, (byte)0xff, 1, 0, 1);
        testInstr8(instr, (byte)0xe0, (byte)0xef, 1, (byte)0xf0, 1, 0, 1);
        testInstr8(instr, (byte)0xf0, (byte)0x00, 1, (byte)0xef, 0, 1, 1);
    }
}
