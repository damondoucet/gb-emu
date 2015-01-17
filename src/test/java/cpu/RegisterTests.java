package cpu;

import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import junit.framework.Assert;
import org.junit.Test;
import util.Util;

/**
 * Tests RegisterState, Register8, and Register16 classes -- all functionality
 * involved with getting and setting register values, both 8-bit and 16-bit.
 */
public class RegisterTests {
    private static Register8[] _r8s = new Register8[] {
            Register8.A, Register8.B, Register8.C, Register8.D,
            Register8.E, Register8.H, Register8.L
    };

    private static Register16[] _r16s = new Register16[] {
            Register16.AF, Register16.BC, Register16.DE,
            Register16.HL, Register16.SP, Register16.PC
    };

    // Initially, all registers should be 0
    private void assertAllRegs0(EmulatorState state) {
        for (Register8 r8 : _r8s)
            Assert.assertEquals(0, (byte)r8.get(state));

        for (Register16 r16 : _r16s)
            Assert.assertEquals(0, (short)r16.get(state));
    }

    private void testRegister8(EmulatorState state, Register8 reg) {
        final byte value = (byte)0x77;

        reg.set(state, value);
        Assert.assertEquals(value, (byte)reg.get(state));
    }

    @Test
    public void test8Bit() {
        EmulatorState state = new EmulatorState();
        assertAllRegs0(state);

        // Test getting and setting each 8-bit register
        for (Register8 r8 : _r8s)
            testRegister8(state, r8);
    }

    private void testRegister16(EmulatorState state, Register16 reg) {
        final short value = (short)0x1337;

        reg.set(state, value);
        Assert.assertEquals(value, (short)reg.get(state));
    }

    @Test
    public void test16Bit() {
        EmulatorState state = new EmulatorState();
        assertAllRegs0(state);

        // Test getting and setting each 16-bit register
        for (Register16 r16 : _r16s)
            testRegister16(state, r16);
    }

    private void testSetting16Modifies8(EmulatorState state,
                                        Register16 reg,
                                        Register8 high,
                                        Register8 low) {
        final short value = (short)0x1337;
        final byte expectedHighByte = (byte)0x13;
        final byte expectedLowByte = (byte)0x37;

        // First test that setting the 16-bit register modifies the two 8-bit
        // registers.
        reg.set(state, value);

        if (high != null)
            Assert.assertEquals(expectedHighByte, (byte)high.get(state));

        if (low != null)
            Assert.assertEquals(expectedLowByte, (byte)low.get(state));
    }

    private void testSetting8Modifies16(EmulatorState state,
                                        Register16 reg,
                                        Register8 high,
                                        Register8 low) {
        final byte highByte = (byte)0x20;
        final byte lowByte = (byte)0x15;

        if (high != null) {
            high.set(state, highByte);

            byte actualHighByte = Util.shortToHighByte(reg.get(state));
            Assert.assertEquals(highByte, actualHighByte);
        }

        if (low != null) {
            low.set(state, lowByte);

            byte actualLowByte = Util.shortToLowByte(reg.get(state));
            Assert.assertEquals(lowByte, actualLowByte);
        }
    }

    // hi and lo here can be null (in the case that that register isn't public).
    // It doesn't make sense to call this if both are null, however.
    private void testConstituentRegs(EmulatorState state,
                                     Register16 reg,
                                     Register8 hi,
                                     Register8 lo) {
        testSetting16Modifies8(state, reg, hi, lo);
        testSetting8Modifies16(state, reg, hi, lo);
    }

    @Test
    public void test8And16BitInterop() {
        EmulatorState state = new EmulatorState();
        assertAllRegs0(state);

        // Test that setting a 16-bit register modifies its constituents, and
        // vice-versa.
        testConstituentRegs(state, Register16.AF, Register8.A, null);
        testConstituentRegs(state, Register16.BC, Register8.B, Register8.C);
        testConstituentRegs(state, Register16.DE, Register8.D, Register8.E);
        testConstituentRegs(state, Register16.HL, Register8.H, Register8.L);
    }
}
