package cpu.disassembler.decoder_tests;

import cpu.disassembler.instructions.CpuControlInstructions;
import org.junit.Test;

/**
 * Test that all CPU Control Instructions are decoded correctly.
 */
public class CpuControlDecoderTests extends DecoderTests {
    @Test
    public void testSimpleCpuControlInstrsDecoded() {
        testDecode(new byte[] { (byte)0x10 }, new CpuControlInstructions.StopInstruction(), 4);
        testDecode(new byte[] { (byte)0x76 }, new CpuControlInstructions.HaltInstruction(), 4);
        testDecode(new byte[] { (byte)0xF3 }, new CpuControlInstructions.DiInstruction(), 4);
        testDecode(new byte[] { (byte)0xFB }, new CpuControlInstructions.EiInstruction(), 4);
    }
}
