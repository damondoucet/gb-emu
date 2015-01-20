package util;

import cpu.disassembler.instruction_args.DereferencedRegisterByte;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.Register8;
import cpu.disassembler.instruction_args.SettableValueContainer;

import java.util.Arrays;
import java.util.List;

/**
 * Utilities for decoding instructions.
 */
public class DecodingUtil {
    private DecodingUtil() {}

    // A lot of instructions (especially arithmetic and bitwise) use the last
    // three bits to describe either the destination register or (HL).
    // This method maps the last three bits of a byte to the value container.
    private final static List<SettableValueContainer<Byte>> _8BitContainers =
            Arrays.asList(Register8.B, Register8.C, Register8.D, Register8.E,
                    Register8.H, Register8.L,
                    new DereferencedRegisterByte(Register16.HL),
                    Register8.A);
    public static SettableValueContainer<Byte> byteContainerFromByte(byte b) {
        return _8BitContainers.get((b & 0xFF) % 8);
    }

    // Particularly useful in the large block of LD commands.
    public static SettableValueContainer<Byte> byteContainerFromIndex(int index) {
        return _8BitContainers.get(index);
    }

    // Most instructions that take an r16 will use them in this order.
    private final static Register16[] _r16ArgsWithAF = new Register16[] {
            Register16.BC, Register16.DE, Register16.HL, Register16.AF
    };

    // However, a few (namely ADD and LD) operate on SP instead of AF.
    private final static Register16[] _r16ArgsWithoutAF = new Register16[] {
            Register16.BC, Register16.DE, Register16.HL, Register16.SP
    };

    // The argument to a 16-bit INC/DEC instruction.
    public static Register16 incDec16Arg(byte b) {
        return _r16ArgsWithoutAF[(b & 0xFF) / 16];
    }

    // The argument to a PUSH/POP instruction.
    public static Register16 pushPopArg(byte b) {
        return _r16ArgsWithAF[((b & 0xFF) - 0xC0) / 16];
    }

    // The argument to a 16-bit ADD HL, * instruction.
    public static Register16 add16Arg(byte b) {
        return _r16ArgsWithoutAF[(b & 0xFF) / 16];
    }

    public static Register16 ld16ConstDest(byte b) {
        return _r16ArgsWithoutAF[(b & 0xFF) / 16];
    }

    // The argument to an 8-bit INC/DEC instruction. This usese the same
    // ordering as _8BitContainers above, but computes the index in a different
    // way.
    public static SettableValueContainer<Byte> incDec8Arg(byte b) {
        // This will truncate and give us the values we want.
        int index = (b & 0xFF) / 8;

        return _8BitContainers.get(index);
    }

    public static SettableValueContainer<Byte> ld8ConstDest(byte b) {
        int index = (b & 0xFF) / 8;
        return _8BitContainers.get(index);
    }
}
