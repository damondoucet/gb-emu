package cpu.disassembler;

import com.google.common.collect.ImmutableList;
import cpu.disassembler.instruction_args.DereferencedRegisterByte;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.SettableValueContainer;
import cpu.disassembler.instructions.BitwiseInstructions;
import util.ByteScanner;
import util.DecodingUtil;

import java.util.List;

/**
 * Used for instructions that span multiple bytes. Note that all command bytes
 * are either one byte long or are two bytes and begin with the prefix 0xCB.
 * This decoder assumes that the prefix 0xCB has already been removed from
 * the stream.
 *
 * http://www.pastraiser.com/cpu/gameboy/gameboy_opcodes.html
 */
public class MultiByteInstructionDecoder extends InstructionDecoder {
    public MultiByteInstructionDecoder() {
        super(INSTRUCTION_FACTORIES);
    }

    private static final List<InstructionFactory> INSTRUCTION_FACTORIES =
            new ImmutableList.Builder<InstructionFactory>()
                    .add(new RlcInstructionFactory())
                    .add(new RrcInstructionFactory())
                    .add(new RlInstructionFactory())
                    .add(new RrInstructionFactory())
                    .add(new SlaInstructionFactory())
                    .add(new SraInstructionFactory())
                    .add(new SwapInstructionFactory())
                    .add(new SrlInstructionFactory())
                    .add(new BitInstructionFactory())
                    .add(new ResInstructionFactory())
                    .add(new SetInstructionFactory())
            .build();

    private static class RlcInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) < 0x08;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.RlcInstruction(container);
        }
    }

    private static class RrcInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x08 &&
                    (nextByte & 0xFF) < 0x10;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.RrcInstruction(container);
        }
    }

    private static class RlInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x10 &&
                    (nextByte & 0xFF) < 0x18;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.RlInstruction(container);
        }
    }

    private static class RrInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x18 &&
                    (nextByte & 0xFF) < 0x20;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.RrInstruction(container);
        }
    }

    private static class SlaInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x20 &&
                    (nextByte & 0xFF) < 0x28;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.SlaInstruction(container);
        }
    }

    private static class SraInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x28 &&
                    (nextByte & 0xFF) < 0x30;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.SraInstruction(container);
        }
    }

    private static class SwapInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x30 &&
                    (nextByte & 0xFF) < 0x38;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.SwapInstruction(container);
        }
    }

    private static class SrlInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x38 &&
                    (nextByte & 0xFF) < 0x40;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.SrlInstruction(container);
        }
    }

    private static class BitInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x40 &&
                    (nextByte & 0xFF) < 0x80;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        private static int bitIndexFromByte(byte b) {
            return ((b & 0xFF) - 0x40) / 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            int bitIndex = bitIndexFromByte(instr);
            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.BitInstruction(bitIndex, container);
        }
    }

    private static class ResInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0x80 &&
                    (nextByte & 0xFF) < 0xC0;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        private static int bitIndexFromByte(byte b) {
            return ((b & 0xFF) - 0x80) / 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            int bitIndex = bitIndexFromByte(instr);
            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);

            return new BitwiseInstructions.ResInstruction(bitIndex, container);
        }
    }

    private static class SetInstructionFactory extends InstructionFactory {
        @Override
        protected boolean canParse(byte nextByte) {
            return (nextByte & 0xFF) >= 0xC0;
        }

        @Override
        public int getMinimumCycles(ByteScanner scanner) {
            return DecodingUtil.byteContainerFromByte(scanner.peek()).equals(
                    new DereferencedRegisterByte(Register16.HL))
                    ? 16 : 8;
        }

        private static int bitIndexFromByte(byte b) {
            return ((b & 0xFF) - 0xC0) / 8;
        }

        @Override
        protected Instruction uncheckedDecodeInstruction(ByteScanner scanner) {
            byte instr = scanner.readByte();

            int bitIndex = bitIndexFromByte(instr);
            SettableValueContainer<Byte> container =
                    DecodingUtil.byteContainerFromByte(instr);


            return new BitwiseInstructions.SetInstruction(bitIndex, container);
        }
    }
}
