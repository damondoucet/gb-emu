package cpu.disassembler;

import cpu.CpuState;
import cpu.disassembler.instruction_args.Register8;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests bitwise instructions:
 *      RLC, RRC, RL, RR, SLA, SRA, SRL, SWAP, BIT, RES, SET
 *
 *  Lots of copy-pasta in this file because I was too lazy to think about
 *  refactoring.
 */
public class BitwiseInstructionTests {
    private final static Register8[] registers = new Register8[] {
        Register8.A, Register8.B, Register8.C, Register8.D, Register8.E,
        Register8.H, Register8.L
    };

    @Test
    public void JavaWhy() {
        byte b = (byte)-1;
        Assert.assertEquals((byte)(b >>> 1), -1);
        Assert.assertEquals((byte)((b & 0xFF) >>> 1), 127);
    }

    private byte computeRlc(byte value) {
        int carry = (value & 0xFF) >>> 7;
        return (byte)((value << 1) | carry);
    }

    private int computeRlcCarry(byte value) {
        return (value & 0xFF) >>> 7;
    }

    @Test
    public void testRlcInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x00, computeRlc((byte)0x00));
        Assert.assertEquals(0, computeRlcCarry((byte)0x00));

        Assert.assertEquals((byte)0x01, computeRlc((byte)0x80));
        Assert.assertEquals(1, computeRlCarry((byte)0x80));

        Assert.assertEquals((byte)0xFF, computeRlc((byte)0xFF));
        Assert.assertEquals(1, computeRlcCarry((byte)0xFF));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeRlc(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.RlcInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format("Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte) r8.get(state));

                Assert.assertEquals(
                        computeRlcCarry(bValue),
                        state.registerState.flags.getC());

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    private byte computeRrc(byte value) {
        return (byte)(((value & 0xFF) >>> 1) | ((value & 1) << 7));
    }

    private int computeRrcCarry(byte value) {
        return value & 1;
    }

    @Test
    public void testRrcInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x80, computeRrc((byte)0x01));
        Assert.assertEquals(1, computeRrcCarry((byte)0x01));

        Assert.assertEquals((byte)0x40, computeRrc((byte)0x80));
        Assert.assertEquals(0, computeRrcCarry((byte)0x80));

        Assert.assertEquals((byte)0xFF, computeRrc((byte)0xFF));
        Assert.assertEquals(1, computeRrcCarry((byte)0xFF));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeRrc(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.RrcInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format("Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte) r8.get(state));

                Assert.assertEquals(
                        computeRrcCarry(bValue),
                        state.registerState.flags.getC());

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    private byte computeRl(byte value, int carry) {
        return (byte)((value << 1) | carry);
    }

    private int computeRlCarry(byte value) {
        return (value >>> 7) & 1;
    }

    @Test
    public void testRlInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x01, computeRl((byte)0x00, 1));
        Assert.assertEquals(0, computeRlCarry((byte)0x00));

        Assert.assertEquals((byte)0x00, computeRl((byte)0x80, 0));
        Assert.assertEquals(1, computeRlCarry((byte)0x80));

        Assert.assertEquals((byte)0xFF, computeRl((byte)0xFF, 1));
        Assert.assertEquals(1, computeRlCarry((byte)0xFF));

        Assert.assertEquals((byte)0xFE, computeRl((byte)0xFF, 0));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            for (int carry = 0; carry <= 1; carry++) {
                byte bValue = (byte)value;
                byte expected = computeRl(bValue, carry);

                for (Register8 r8 : registers) {
                    state.registerState.flags.setC(carry);
                    r8.set(state, bValue);

                    new Instructions.RlInstruction(r8).execute(state);
                    Assert.assertEquals(
                            String.format("Register %s, value %d (C=%d)",
                                    r8.toString(),
                                    value,
                                    carry),
                            expected, (byte) r8.get(state));

                    Assert.assertEquals(
                            computeRlCarry(bValue),
                            state.registerState.flags.getC());

                    int isZero = expected == 0 ? 1 : 0;
                    Assert.assertEquals(isZero, state.registerState.flags.getZ());
                }
            }
        }
    }

    private byte computeRr(byte value, int carry) {
        int shiftedCarry = carry << 7;
        return (byte)(shiftedCarry | ((value & 0xFF) >>> 1));
    }

    private int computeRrCarry(byte value) {
        return value & 1;
    }

    @Test
    public void testRrInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x80, computeRr((byte) 0x00, 1));
        Assert.assertEquals(0, computeRrCarry((byte) 0x00));

        Assert.assertEquals((byte)0x00, computeRr((byte)0x00, 0));
        Assert.assertEquals(0, computeRrCarry((byte)0x00));

        Assert.assertEquals((byte)0xFF, computeRr((byte)0xFF, 1));
        Assert.assertEquals(1, computeRrCarry((byte)0xFF));

        Assert.assertEquals((byte)0x7F, computeRr((byte) 0xFF, 0));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            for (int carry = 0; carry <= 1; carry++) {
                byte bValue = (byte)value;
                byte expected = computeRr(bValue, carry);

                for (Register8 r8 : registers) {
                    state.registerState.flags.setC(carry);
                    r8.set(state, bValue);

                    new Instructions.RrInstruction(r8).execute(state);
                    Assert.assertEquals(
                            String.format("Register %s, value %d (C=%d)",
                                    r8.toString(),
                                    value,
                                    carry),
                            expected, (byte) r8.get(state));

                    Assert.assertEquals(
                            computeRrCarry(bValue),
                            state.registerState.flags.getC());

                    int isZero = expected == 0 ? 1 : 0;
                    Assert.assertEquals(isZero, state.registerState.flags.getZ());
                }
            }
        }
    }

    private byte computeSla(byte value) {
        return (byte)(value << 1);
    }

    private int computeSlaCarry(byte value) {
        return (value >>> 7) & 1;
    }

    @Test
    public void testSlaInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x02, computeSla((byte)0x01));
        Assert.assertEquals(0, computeSlaCarry((byte)0x01));

        Assert.assertEquals((byte)0x00, computeSla((byte)0x80));
        Assert.assertEquals(1, computeSlaCarry((byte)0x80));

        Assert.assertEquals((byte)0xFE, computeSla((byte)0xFF));
        Assert.assertEquals(1, computeSlaCarry((byte)0xFF));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeSla(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.SlaInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format("Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte) r8.get(state));

                Assert.assertEquals(
                        computeSlaCarry(bValue),
                        state.registerState.flags.getC());

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    private byte computeSrl(byte value) {
        return (byte)((value & 0xFF) >>> 1);
    }

    private int computeSrlCarry(byte value) {
        return value & 1;
    }

    @Test
    public void testSrlInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0x00, computeSrl((byte)0x01));
        Assert.assertEquals(1, computeSrlCarry((byte)0x01));

        Assert.assertEquals((byte)0x08, computeSrl((byte)0x10));
        Assert.assertEquals(0, computeSrlCarry((byte)0x10));

        Assert.assertEquals((byte)0x7F, computeSrl((byte)0xFF));
        Assert.assertEquals(1, computeSrlCarry((byte)0xFF));

        // Now try all possible values with all registers
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeSrl(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.SrlInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format("Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte) r8.get(state));

                Assert.assertEquals(
                        computeSrlCarry(bValue),
                        state.registerState.flags.getC());

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    private byte computeSra(byte value) {
        return (byte)(value >> 1);
    }

    private int computeSraCarry(byte value) {
        return value & 1;
    }

    @Test
    public void testSraInstruction() {
        // First test that our compute functions are correct.
        Assert.assertEquals((byte)0xFF, computeSra((byte)0xFF));
        Assert.assertEquals(1, computeSraCarry((byte)0xFF));

        Assert.assertEquals((byte)0x08, computeSra((byte)0x10));
        Assert.assertEquals(0, computeSraCarry((byte)0x10));

        Assert.assertEquals((byte)0x02, computeSra((byte)0x05));
        Assert.assertEquals(1, computeSraCarry((byte)0x05));

        // Now try all possible values with all registers.
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeSra(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.SraInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format("Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte)r8.get(state));

                Assert.assertEquals(
                        computeSraCarry(bValue),
                        state.registerState.flags.getC());

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    private byte computeSwap(byte value) {
        int highNibble = value >>> 4;
        int lowNibble = value & 0xF;

        return (byte)((lowNibble << 4) | highNibble);
    }

    @Test
    public void testSwapInstruction() {
        // Check that our helper function is correct.
        Assert.assertEquals((byte)0xFE, computeSwap((byte)0xEF));
        Assert.assertEquals((byte)0x12, computeSwap((byte)0x21));
        Assert.assertEquals((byte)0x00, computeSwap((byte)0x00));

        // Now try all possible values with all registers.
        CpuState state = new CpuState();

        // We use an int here and cast because all bytes are less than 256.
        for (int value = 0; value < 256; value++) {
            byte bValue = (byte)value;
            byte expected = computeSwap(bValue);

            for (Register8 r8 : registers) {
                r8.set(state, bValue);

                new Instructions.SwapInstruction(r8).execute(state);
                Assert.assertEquals(
                        String.format(
                                "Register %s, value %d",
                                r8.toString(),
                                value),
                        expected, (byte)r8.get(state));

                int isZero = expected == 0 ? 1 : 0;
                Assert.assertEquals(isZero, state.registerState.flags.getZ());
            }
        }
    }

    // Expects all registers to have value 0. Leaves all registers with value
    // 0xFF
    private void testSetAndBitInstructions(CpuState state) {
        byte previousValue = 0;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            byte newValue = (byte)(previousValue | (1 << bitIndex));

            for (Register8 r8 : registers) {
                Assert.assertEquals(previousValue, (byte)r8.get(state));

                // Test setting the bit.
                new Instructions.SetInstruction(bitIndex, r8).execute(state);
                Assert.assertEquals(newValue, (byte)r8.get(state));

                // Test checking the bit.
                new Instructions.BitInstruction(bitIndex, r8).execute(state);
                Assert.assertEquals(0, state.registerState.flags.getZ());
                Assert.assertEquals(0, state.registerState.flags.getN());
                Assert.assertEquals(1, state.registerState.flags.getH());
            }

            previousValue = newValue;
        }
    }

    // Expects all registers to have value 0xFF. Leaves all registers with
    // value 0.
    private void testResetAndBitInstructions(CpuState state) {
        byte previousValue = (byte)0xFF;

        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            byte newValue = (byte)(previousValue & ~(1 << bitIndex));

            for (Register8 r8 : registers) {
                Assert.assertEquals(previousValue, (byte)r8.get(state));

                // Test resetting the bit.
                new Instructions.ResInstruction(bitIndex, r8).execute(state);
                Assert.assertEquals(newValue, (byte)r8.get(state));

                // Test checking the bit.
                new Instructions.BitInstruction(bitIndex, r8).execute(state);
                Assert.assertEquals(1, state.registerState.flags.getZ());
                Assert.assertEquals(0, state.registerState.flags.getN());
                Assert.assertEquals(1, state.registerState.flags.getH());
            }

            previousValue = newValue;
        }
    }

    @Test
    public void testSetResetInstructions() {
        CpuState state = new CpuState();
        testSetAndBitInstructions(state);
        testResetAndBitInstructions(state);
    }
}
