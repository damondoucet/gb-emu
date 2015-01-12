package cpu;

import junit.framework.Assert;
import org.junit.Test;
import util.Util;

/**
 * Tests RegisterState, Register8, and Register16 classes -- all functionality
 * involved with getting and setting register values, both 8-bit and 16-bit.
 */
public class RegisterTests {
    // Initially, all registers should be 0
    private void assertAllRegs0(RegisterState state) {
        Assert.assertEquals(Register8.A.get(state), 0);
        Assert.assertEquals(Register8.B.get(state), 0);
        Assert.assertEquals(Register8.C.get(state), 0);
        Assert.assertEquals(Register8.D.get(state), 0);
        Assert.assertEquals(Register8.E.get(state), 0);
        Assert.assertEquals(Register8.H.get(state), 0);
        Assert.assertEquals(Register8.L.get(state), 0);

        Assert.assertEquals(Register16.AF.get(state), 0);
        Assert.assertEquals(Register16.BC.get(state), 0);
        Assert.assertEquals(Register16.DE.get(state), 0);
        Assert.assertEquals(Register16.HL.get(state), 0);
        Assert.assertEquals(Register16.SP.get(state), 0);
        Assert.assertEquals(Register16.PC.get(state), 0);
    }

    private void testRegister8(RegisterState state, Register8 reg) {
        final byte value = (byte)0x77;

        reg.set(state, value);
        Assert.assertEquals(value, reg.get(state));
    }

    @Test
    public void test8Bit() {
        RegisterState state = new RegisterState();
        assertAllRegs0(state);

        // Test getting and setting each 8-bit register
        testRegister8(state, Register8.A);
        testRegister8(state, Register8.B);
        testRegister8(state, Register8.C);
        testRegister8(state, Register8.D);
        testRegister8(state, Register8.E);
        testRegister8(state, Register8.H);
        testRegister8(state, Register8.L);
    }

    private void testRegister16(RegisterState state, Register16 reg) {
        final short value = (short)0x1337;

        reg.set(state, value);
        Assert.assertEquals(reg.get(state), value);
    }

    @Test
    public void test16Bit() {
        RegisterState state = new RegisterState();
        assertAllRegs0(state);

        // Test getting and setting each 16-bit register
        testRegister16(state, Register16.AF);
        testRegister16(state, Register16.BC);
        testRegister16(state, Register16.DE);
        testRegister16(state, Register16.HL);
        testRegister16(state, Register16.SP);
        testRegister16(state, Register16.PC);
    }

    private void testSetting16Modifies8(RegisterState state,
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
            Assert.assertEquals(expectedHighByte, high.get(state));

        if (low != null)
            Assert.assertEquals(expectedLowByte, low.get(state));
    }

    private void testSetting8Modifies16(RegisterState state,
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
    private void testConstituentRegs(RegisterState state,
                                     Register16 reg,
                                     Register8 hi,
                                     Register8 lo) {
        testSetting16Modifies8(state, reg, hi, lo);
        testSetting8Modifies16(state, reg, hi, lo);
    }

    @Test
    public void test8And16BitInterop() {
        RegisterState state = new RegisterState();
        assertAllRegs0(state);

        // Test that setting a 16-bit register modifies its constituents, and
        // vice-versa.
        testConstituentRegs(state, Register16.AF, Register8.A, null);
        testConstituentRegs(state, Register16.BC, Register8.B, Register8.C);
        testConstituentRegs(state, Register16.DE, Register8.D, Register8.E);
        testConstituentRegs(state, Register16.HL, Register8.H, Register8.L);
    }
}
