package cpu;

import cpu.disassembler.instruction_args.Register16;
import junit.framework.Assert;
import org.junit.Test;

public class FlagsTests {
    private void testSettingFlags(RegisterState state, int value) {
        state.flags.setZ(value);
        Assert.assertEquals(value, state.flags.getZ());

        state.flags.setH(value);
        Assert.assertEquals(value, state.flags.getH());

        state.flags.setC(value);
        Assert.assertEquals(value, state.flags.getC());

        state.flags.setN(value);
        Assert.assertEquals(value, state.flags.getN());
    }

    @Test
    public void testClearSetRetrieve() {
        RegisterState state = new RegisterState();

        Assert.assertEquals(0, state.flags.getZ());
        Assert.assertEquals(0, state.flags.getH());
        Assert.assertEquals(0, state.flags.getC());
        Assert.assertEquals(0, state.flags.getN());

        // Setting a flag should be reflected in its get
        testSettingFlags(state, 1);

        // Clearing a flag should be reflected in its get
        testSettingFlags(state, 0);
    }

    @Test
    public void testAFRegisterUpdated() {
        // Test setting the flags in the order Z, H, C, N, and check the value
        // of AF after each set.
        final short expectedAfterZ = 1 << 7;
        final short expectedAfterN = expectedAfterZ | (1 << 6);
        final short expectedAfterH = expectedAfterN | (1 << 5);
        final short expectedAfterC = expectedAfterH | (1 << 4);

        EmulatorState state = new EmulatorState();
        Flags flags = state.registerState.flags;
        Assert.assertEquals(0, (short) Register16.AF.get(state));

        flags.setZ(1);
        Assert.assertEquals(expectedAfterZ, (short)Register16.AF.get(state));

        flags.setN(1);
        Assert.assertEquals(expectedAfterN, (short)Register16.AF.get(state));

        flags.setH(1);
        Assert.assertEquals(expectedAfterH, (short)Register16.AF.get(state));

        flags.setC(1);
        Assert.assertEquals(expectedAfterC, (short)Register16.AF.get(state));
    }
}
