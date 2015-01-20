package cpu.disassembler;

import com.google.common.collect.ImmutableList;
import cpu.disassembler.instruction_args.*;
import cpu.disassembler.instructions.*;
import util.ByteScanner;
import util.DecodingUtil;

import java.util.List;

/**
 * This is the decoder that should be used when decoding instructions. It wraps
 * the MultiByteInstructionDecoder and will parse those when they appear in the
 * stream.
 *
 * http://www.pastraiser.com/cpu/gameboy/gameboy_opcodes.html
 *
 * TODO(ddoucet): add factories to list
 */
public class RootInstructionDecoder extends InstructionDecoder {
    public RootInstructionDecoder() {
        super(INSTRUCTION_FACTORIES);
    }

    private static final List<InstructionFactory> INSTRUCTION_FACTORIES =
            new ImmutableList.Builder<InstructionFactory>()
                    .add(new MultiByteInstructionFactory())
                    .add(new NopInstructionFactory())
                    .add(new DiInstructionFactory())
                    .add(new EiInstructionFactory())
                    .add(new CcfInstructionFactory())
                    .add(new ScfInstructionFactory())
                    .add(new DaaInstructionFactory())
                    .add(new CplInstructionFactory())
                    .add(new StopInstructionFactory())
                    .add(new HaltInstructionFactory())
                    .add(new RlcaInstructionFactory())
                    .add(new RlaInstructionFactory())
                    .add(new RrcaInstructionFactory())
                    .add(new RraInstructionFactory())
                    .add(new Add16InstructionFactory())
                    .add(new Inc8InstructionFactory())
                    .add(new Dec8InstructionFactory())
                    .add(new Inc16InstructionFactory())
                    .add(new Dec16InstructionFactory())
                    .add(new ArithInstructionFactory())
                    .add(new Ld8RegInstructionFactory())
                    .add(new LdhInstructionFactory())
                    .add(new LdPointerAInstructionFactory())
                    .add(new LdSpConstInstructionFactory())
                    .add(new Ld8ConstInstructionFactory())
                    .add(new Ld16ConstInstructionFactory())
                    .add(new LdIoInstructionFactory())
                    .add(new LdiInstructionFactory())
                    .add(new LddInstructionFactory())
                    .add(new LdSpInstructionFactory())
                    .add(new JrInstructionFactory())
                    .add(new RetInstructionFactory())
                    .add(new ConditionalRetInstructionFactory())
                    .add(new JpInstructionFactory())
                    .add(new ConditionalJpInstructionFactory())
                    .add(new CallInstructionFactory())
                    .add(new PushInstructionFactory())
                    .add(new PopInstructionFactory())
                    .add(new RstInstructionFactory())
            .build();

    private static class MultiByteInstructionFactory extends InstructionFactory {
        private final MultiByteInstructionDecoder _multiByteInstructionDecoder;

        public MultiByteInstructionFactory() {
            _multiByteInstructionDecoder = new MultiByteInstructionDecoder();
        }

        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xCB;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // Adjust the scanner forward one byte so that the next decoders
            // can read with the correct positioning.
            scanner.readByte();
            int cycles = _multiByteInstructionDecoder.getMinimumCycles(scanner);

            // Reset the scanner to its original position for the decoder.
            scanner.seekOffset(-1);

            return cycles;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();  // the decoder expects the 0xCB to be cleared

            return _multiByteInstructionDecoder.decodeNext(scanner);
        }
    }

    private static class NopInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x00;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new PCControlInstructions.NopInstruction();
        }
    }

    private static class DiInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xF3;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new CpuControlInstructions.DiInstruction();
        }
    }

    private static class EiInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xFB;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new CpuControlInstructions.EiInstruction();
        }
    }

    private static class CcfInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x3F;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new ArithmeticInstructions.CcfInstruction();
        }
    }

    private static class ScfInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x37;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new ArithmeticInstructions.ScfInstruction();
        }
    }

    private static class DaaInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x27;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new ArithmeticInstructions.DaaInstruction();
        }
    }

    private static class CplInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x2F;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new BitwiseInstructions.CplInstruction();
        }
    }

    private static class StopInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x10;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            // TODO(ddoucet): there's something weird about STOP being 0x10 0x00
            // or just 0x10. I think this should be fine, but I should document it.
            scanner.readByte();
            return new CpuControlInstructions.StopInstruction();
        }
    }

    private static class HaltInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x76;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new CpuControlInstructions.HaltInstruction();
        }
    }

    private static class RlcaInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x07;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new BitwiseInstructions.RlcaInstruction();
        }
    }

    private static class RlaInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x17;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new BitwiseInstructions.RlaInstruction();
        }
    }

    private static class RrcaInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x0F;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new BitwiseInstructions.RrcaInstruction();
        }
    }

    private static class RraInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x1F;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            return new BitwiseInstructions.RraInstruction();
        }
    }

    private static class Inc8InstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) < 0x40 &&
                    ((nextByte & 0xFF) % 16 == 4 ||
                            (nextByte & 0xFF) % 16 == 0xC);
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.incDec8Arg(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 12 : 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new ArithmeticInstructions.Inc8Instruction(
                    DecodingUtil.incDec8Arg(scanner.readByte()));
        }
    }

    private static class Dec8InstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) < 0x40 &&
                    (nextByte & 0xFF) % 8 == 5;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.incDec8Arg(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 12 : 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new ArithmeticInstructions.Dec8Instruction(
                    DecodingUtil.incDec8Arg(scanner.readByte()));
        }
    }

    private static class Inc16InstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) < 0x40 && (nextByte & 0xFF) % 16 == 3;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new ArithmeticInstructions.Inc16Instruction(
                    DecodingUtil.incDec16Arg(scanner.readByte()));
        }
    }

    private static class Dec16InstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) < 0x40 && (nextByte & 0xFF) % 16 == 0xB;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new ArithmeticInstructions.Dec16Instruction(
                    DecodingUtil.incDec16Arg(scanner.readByte()));
        }
    }

    private static class Add16InstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xE8 ||
                    ((nextByte & 0xFF) < 0x40 &&
                        (nextByte & 0xFF) % 16 == 9);
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return (scanner.peek() & 0xFF) == 0xE8 ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            if ((command & 0xFF) == 0xE8)
                return new ArithmeticInstructions.Add16Instruction(
                        Register16.SP, new ShortConst(scanner.readByte()));

            return new ArithmeticInstructions.Add16Instruction(
                    Register16.HL, DecodingUtil.add16Arg(command));
        }
    }

    // ADD,ADC,SUB,SBC,AND,XOR,OR,CP instructions
    private static class ArithInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return ((nextByte & 0xFF) >= 0x80 &&
                    (nextByte & 0xFF) < 0xC0) ||
                    ((nextByte & 0xFF) >= 0xC0 &&
                            ((nextByte & 0xFF) % 16 == 6 ||
                            (nextByte & 0xFF) % 16 == 0xE));
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // Second hexits 0x06 and 0x0E correspond to either having a (HL)
            // arg, which requires 4 cycles for reading from memory; or it
            // corresponds to the const operations, which take an extra 4
            // cycles to read the next byte in the instruction.
            return (scanner.peek() & 0xFF) % 8 == 6 ? 8 : 4;
        }

        private static Instruction instrFromIndexArgs(
                int index, ValueContainer<Byte> src) {
            // This is a little lame :/
            switch (index) {
                case 0:
                    return new ArithmeticInstructions.Add8Instruction(src);
                case 1:
                    return new ArithmeticInstructions.AdcInstruction(src);
                case 2:
                    return new ArithmeticInstructions.SubInstruction(src);
                case 3:
                    return new ArithmeticInstructions.SbcInstruction(src);
                case 4:
                    return new BitwiseInstructions.AndInstruction(src);
                case 5:
                    return new BitwiseInstructions.XorInstruction(src);
                case 6:
                    return new BitwiseInstructions.OrInstruction(src);
                case 7:
                    return new ArithmeticInstructions.CpInstruction(src);
                default:
                    throw new IllegalArgumentException("Illegal index: " + Integer.toString(index));
            }
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            int index;
            ValueContainer<Byte> src;

            if ((command & 0xFF) < 0xC0) {
                index = ((command & 0xFF) - 0x80) / 8;
                src = DecodingUtil.byteContainerFromByte(command);
            } else {
                // This truncates and works out to be the correct value.
                index = ((command & 0xFF) - 0xC0) / 8;
                src = new ByteConst(scanner.readByte());
            }

            return instrFromIndexArgs(index, src);
        }
    }

    // All LD instructions of the form LD r8, r8 (where r8 is A..L and (HL))
    private static class Ld8RegInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            // Note that we exclude 0x76, which is HALT, because that would
            // have been LD (HL), (HL), which is illegal in most CPUs.
            return (nextByte & 0xFF) >= 0x40 &&
                    (nextByte & 0xFF) < 0x80 &&
                    (nextByte & 0xFF) != 0x76;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // See comment in ArithInstructionFactory.getMinimumCycles()
            return (scanner.peek() & 0xFF) % 8 == 6 ||
                    ((scanner.peek() & 0xFF) - 0x40) / 8 == 6 ? 8 : 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            SettableValueContainer<Byte> dest = DecodingUtil.byteContainerFromIndex(
                    ((command & 0xFF) - 0x40) / 8);

            ValueContainer<Byte> src = DecodingUtil.byteContainerFromByte(command);

            return new MemoryInstructions.Ld8Instruction(dest, src);
        }
    }

    // LDH (a8), A and LDH A, (a8) are mnemonics for LD (FF00+a8), A and LD A, (FF00+a8)
    private static class LdhInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xE0 ||
                    (nextByte & 0xFF) == 0xF0;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 12;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            short addr = (short)(0xFF00 + scanner.readByte());
            SettableValueContainer<Byte> container = new BytePointer(addr);

            return (command & 0xFF) == 0xE0
                    ? new MemoryInstructions.Ld8Instruction(container, Register8.A)
                    : new MemoryInstructions.Ld8Instruction(Register8.A, container);
        }
    }

    private static class LdPointerAInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x02 ||
                    (nextByte & 0xFF) == 0x12 ||
                    (nextByte & 0xFF) == 0x0A ||
                    (nextByte & 0xFF) == 0x1A ||
                    (nextByte & 0xFF) == 0xEA ||
                    (nextByte & 0xFF) == 0xFA;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return (scanner.peek() & 0xFF) >= 0xE0 ? 16 : 8;
        }

        private static boolean aIsDest(byte command) {
            return (command & 0xFF) % 16 == 0xA &&
                    (command & 0xFF) / 16 != 0xE;
        }

        private static SettableValueContainer<Byte> containerFromByte(
                ByteScanner scanner, byte command) {
            if ((command & 0xFF) / 16 == 0)
                return new DereferencedRegisterByte(Register16.BC);
            if ((command & 0xFF) / 16 == 1)
                return new DereferencedRegisterByte(Register16.DE);

            return new BytePointer(scanner.readLittleEndianShort());
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            SettableValueContainer<Byte> container = containerFromByte(scanner, command);

            return aIsDest(command)
                    ? new MemoryInstructions.Ld8Instruction(Register8.A, container)
                    : new MemoryInstructions.Ld8Instruction(container, Register8.A);
        }
    }

    private static class LdSpConstInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x08;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 20;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            scanner.readByte();
            short addr = scanner.readLittleEndianShort();
            SettableValueContainer<Short> container = new ShortPointer(addr);

            return new MemoryInstructions.Ld16Instruction(
                    container, Register16.SP);
        }
    }

    private static class Ld8ConstInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) <= 0x40 &&
                    (nextByte & 0xFF) % 8 == 6;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // 0x36 is LD (HL), d8, which requires a memory reference.
            return (scanner.peek() & 0xFF) == 0x36 ? 12 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            ValueContainer<Byte> container = new ByteConst(scanner.readByte());

            return new MemoryInstructions.Ld8Instruction(
                    DecodingUtil.ld8ConstDest(command), container);
        }
    }

    private static class Ld16ConstInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) <= 0x40 &&
                    (nextByte & 0xFF) % 16 == 1;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 12;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            ValueContainer<Short> container = new ShortConst(scanner.readLittleEndianShort());

            return new MemoryInstructions.Ld16Instruction(
                    DecodingUtil.ld16ConstDest(command), container);
        }
    }

    private static class LdIoInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xE2 ||
                    (nextByte & 0xFF) == 0xF2;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            return (command & 0xFF) == 0xE2
                    ? MemoryInstructions.LdIoPortInstruction.LdToIoPort()
                    : MemoryInstructions.LdIoPortInstruction.LdFromIoPort();
        }
    }

    private static class LdiInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x22 ||
                    (nextByte & 0xFF) == 0x2A;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            return (command & 0xFF) == 0x22
                    ? MemoryInstructions.LdiInstruction.ldiToHL()
                    : MemoryInstructions.LdiInstruction.ldiFromHL();
        }
    }

    private static class LddInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x32 ||
                    (nextByte & 0xFF) == 0x3A;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            return (command & 0xFF) == 0x32
                    ? MemoryInstructions.LddInstruction.lddToHL()
                    : MemoryInstructions.LddInstruction.lddFromHL();
        }
    }

    private static class LdSpInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xF8 ||
                    (nextByte & 0xFF) == 0xF9;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return (scanner.peek() & 0xFF) == 0xF8 ? 12 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            if ((command & 0xFF) == 0xF8)
                return new MemoryInstructions.LdSpToHLInstruction(scanner.readByte());

            return new MemoryInstructions.LdHLToSpInstruction();
        }
    }

    private static class JrInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0x18 ||
                    (nextByte & 0xFF) == 0x28 ||
                    (nextByte & 0xFF) == 0x38 ||
                    (nextByte & 0xFF) == 0x20 ||
                    (nextByte & 0xFF) == 0x30;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // More cycles if the jump is taken; 0x18 is the unconditional JR,
            // whereas the others will add the additional cycles only if the
            // condition is met.
            return (scanner.peek() & 0xFF) == 0x18 ? 12 : 8;
        }

        private static PCControlInstructions.JumpFlag jumpFlagFromByte(byte command) {
            if (command == 0x18)
                return PCControlInstructions.JumpFlag.None;
            if ((command & 0xFF) / 16 == 2)
                return PCControlInstructions.JumpFlag.Z;
            return PCControlInstructions.JumpFlag.C;
        }

        private static boolean isNegated(byte command) {
            return (command & 0xFF) % 16 == 0;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            byte offset = scanner.readByte();

            return new PCControlInstructions.JrInstruction(
                    jumpFlagFromByte(command), isNegated(command), offset);
        }
    }

    private static class RetInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xC9 ||
                    (nextByte & 0xFF) == 0xD9;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 16;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            if ((command & 0xFF) == 0xC9)
                return new PCControlInstructions.RetInstruction(
                        PCControlInstructions.JumpFlag.None, false);

            return new PCControlInstructions.RetiInstruction();
        }
    }

    private static class ConditionalRetInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xC0 ||
                    (nextByte & 0xFF) == 0xD0 ||
                    (nextByte & 0xFF) == 0xC8 ||
                    (nextByte & 0xFF) == 0xD8;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 8;
        }

        private static PCControlInstructions.JumpFlag jumpFlagFromByte(byte command) {
            return (command & 0xFF) / 16 == 0xC
                    ? PCControlInstructions.JumpFlag.Z
                    : PCControlInstructions.JumpFlag.C;
        }

        private static boolean isNegated(byte command) {
            return (command & 0xFF) % 16 == 0;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();

            PCControlInstructions.JumpFlag flag = jumpFlagFromByte(command);
            boolean negated = isNegated(command);

            return new PCControlInstructions.RetInstruction(flag, negated);
        }
    }

    private static class JpInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xC3 ||
                    (nextByte & 0xFF) == 0xE9;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // 0xC3 is JP a16, which requires reading an address;
            // 0xE9 is simply JP (HL)
            return (scanner.peek() & 0xFF) == 0xC3 ? 16 : 4;
        }

        private static ValueContainer<Short> readAddress(ByteScanner scanner, byte command) {
            if ((command & 0xFF) == 0xE9)
                return new DereferencedRegisterShort(Register16.HL);

            return new ShortConst(scanner.readLittleEndianShort());
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            ValueContainer<Short> container = readAddress(scanner, command);

            return new PCControlInstructions.JpInstruction(
                    PCControlInstructions.JumpFlag.None, false, container);
        }
    }

    private static class ConditionalJpInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xC2 ||
                    (nextByte & 0xFF) == 0xD2 ||
                    (nextByte & 0xFF) == 0xCA ||
                    (nextByte & 0xFF) == 0xDA;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 12;
        }

        private static PCControlInstructions.JumpFlag jumpFlagFromByte(byte command) {
            return (command & 0xFF) / 16 == 0xC
                    ? PCControlInstructions.JumpFlag.Z
                    : PCControlInstructions.JumpFlag.C;
        }

        private static boolean isNegated(byte command) {
            return (command & 0xFF) % 16 == 2;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            short addr = scanner.readLittleEndianShort();
            ValueContainer<Short> container = new ShortConst(addr);

            PCControlInstructions.JumpFlag flag = jumpFlagFromByte(command);
            boolean negated = isNegated(command);

            return new PCControlInstructions.JpInstruction(flag, negated, container);
        }
    }

    private static class CallInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) == 0xC4 ||
                    (nextByte & 0xFF) == 0xD4 ||
                    (nextByte & 0xFF) == 0xCC ||
                    (nextByte & 0xFF) == 0xDC ||
                    (nextByte & 0xFF) == 0xCD;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            // More cycles if the jump is taken; 0x18 is the unconditional CALL,
            // whereas the others will add the additional cycles only if the
            // condition is met.
            return (scanner.peek() & 0xFF) == 0xCD ? 24 : 12;
        }

        private static PCControlInstructions.JumpFlag jumpFlagFromByte(byte command) {
            if ((command & 0xFF) == 0xCD)
                return PCControlInstructions.JumpFlag.None;
            if ((command & 0xFF) / 16 == 0xC)
                return PCControlInstructions.JumpFlag.Z;
            return PCControlInstructions.JumpFlag.C;
        }

        private static boolean isNegated(byte command) {
            return (command & 0xFF) % 16 == 4;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte command = scanner.readByte();
            short addr = scanner.readLittleEndianShort();
            ValueContainer<Short> container = new ShortConst(addr);

            PCControlInstructions.JumpFlag flag = jumpFlagFromByte(command);
            boolean negated = isNegated(command);

            return new PCControlInstructions.CallInstruction(flag, negated, container);
        }
    }

    private static class PopInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0xC0 && (nextByte & 0xFF) % 16 == 1;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 12;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new MemoryInstructions.PopInstruction(
                    DecodingUtil.pushPopArg(scanner.readByte()));
        }
    }

    private static class PushInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0xC0 && (nextByte & 0xFF) % 16 == 5;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 16;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            return new MemoryInstructions.PushInstruction(
                    DecodingUtil.pushPopArg(scanner.readByte()));
        }
    }

    private static class RstInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0xC0 && (nextByte & 7) == 7;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return 16;
        }

        private static int computeFirstHexit(byte b) {
            return ((b & 0xFF) - 0xC0) / 16;
        }

        private static int computeSecondHexit(byte b) {
            return (b & 0xFF) % 16 == 7 ? 0 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte b = scanner.readByte();
            int first = computeFirstHexit(b);
            int second = computeSecondHexit(b);
            byte restartVector = (byte)((first << 4) | second);

            return new PCControlInstructions.RstInstruction(restartVector);
        }
    }
}
