package cpu.disassembler.instructions;

import cpu.EmulatorState;
import cpu.disassembler.Instruction;
import cpu.disassembler.instruction_args.Register16;
import cpu.disassembler.instruction_args.ValueContainer;
import util.Util;

import java.util.Objects;

/**
 * Instructions for controlling the instruction pointer:
 *      NOP,
 *      JP, JR,
 *      CALL, RST
 */
public final class IPControlInstructions {
    private IPControlInstructions() {}

    public static class NopInstruction implements Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass().equals(rhs.getClass());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(toString());
        }

        @Override
        public String toString() {
            return "NOP";
        }

        @Override
        public void execute(EmulatorState state) {}
    }

    enum JumpFlag {
        Z,
        C,
        None
    }

    private static boolean shouldJump(EmulatorState state, JumpFlag flag, boolean negated) {
        if (flag == JumpFlag.Z)
            return (state.registerState.flags.getZ() == 1) != negated;
        else if (flag == JumpFlag.C)
            return (state.registerState.flags.getC() == 1) != negated;
        else if (flag == JumpFlag.None)
            return true;
        else
            throw new IllegalArgumentException("Illegal value of flag: " + flag.toString());
    }

    private static String computeFlagString(JumpFlag flag, boolean negated) {
        return flag == JumpFlag.None
                ? ""
                : (negated ? "N" : "") + flag.toString();
    }

    private static String jumpString(String mnemonic, JumpFlag flag, boolean negated, String address) {
        String flagStr = computeFlagString(flag, negated);
        if (!flagStr.equals(""))
            flagStr += ", ";

        return String.format("%s %s%s",
                mnemonic,
                flagStr,
                address);
    }

    private static void call(EmulatorState state, short address) {
        MemoryInstructions.push(state, address);
        Register16.PC.set(state, address);
    }

    private static void ret(EmulatorState state) {
        short returnAddr = MemoryInstructions.pop(state);
        Register16.PC.set(state, returnAddr);
    }

    public static class JpInstruction implements Instruction {
        private final JumpFlag _flag;
        private final boolean _negated;
        private final ValueContainer<Short> _address;

        public JpInstruction(JumpFlag flag, boolean negated, ValueContainer<Short> address) {
            _flag = flag;
            _negated = negated;
            _address = address;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            JpInstruction other = (JpInstruction)rhs;
            return _flag == other._flag &&
                    _negated == other._negated &&
                    _address == other._address;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_flag, _negated, _address);
        }

        @Override
        public String toString() {
            return jumpString("JP", _flag, _negated, _address.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            if (shouldJump(state, _flag, _negated))
                Register16.PC.set(state, _address.get(state));
        }
    }

    public static class JrInstruction implements Instruction {
        private final JumpFlag _flag;
        private final boolean _negated;
        private final ValueContainer<Short> _offset;

        public JrInstruction(JumpFlag flag, boolean negated, ValueContainer<Short> offset) {
            _flag = flag;
            _negated = negated;
            _offset = offset;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            JrInstruction other = (JrInstruction)rhs;
            return _flag == other._flag &&
                    _negated == other._negated &&
                    _offset == other._offset;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_flag, _negated, _offset);
        }

        @Override
        public String toString() {
            return jumpString("JR", _flag, _negated, _offset.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            if (shouldJump(state, _flag, _negated)) {
                short newAddr = (short)(Register16.PC.get(state) + _offset.get(state));
                Register16.PC.set(state, newAddr);
            }
        }
    }

    public static class CallInstruction implements Instruction {
        private final JumpFlag _flag;
        private final boolean _negated;
        private final ValueContainer<Short> _address;

        public CallInstruction(JumpFlag flag, boolean negated, ValueContainer<Short> address) {
            _flag = flag;
            _negated = negated;
            _address = address;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            CallInstruction other = (CallInstruction)rhs;
            return _flag == other._flag &&
                    _negated == other._negated &&
                    _address == other._address;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_flag, _negated, _address);
        }

        @Override
        public String toString() {
            return jumpString("CALL", _flag, _negated, _address.toString());
        }

        @Override
        public void execute(EmulatorState state) {
            if (shouldJump(state, _flag, _negated))
                call(state, _address.get(state));
        }
    }

    public static class RstInstruction implements Instruction {
        private final byte _addr;

        public RstInstruction(byte addr) {
            _addr = addr;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RstInstruction other = (RstInstruction)rhs;
            return _addr == other._addr;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_addr);
        }

        @Override
        public String toString() {
            return String.format("RST %s", Util.byteToHexString(_addr));
        }

        @Override
        public void execute(EmulatorState state) {
            call(state, _addr);
        }
    }

    public static class RetInstruction implements Instruction {
        private final JumpFlag _flag;
        private final boolean _negated;

        public RetInstruction(JumpFlag flag, boolean negated) {
            _flag = flag;
            _negated = negated;
        }

        @Override
        public boolean equals(Object rhs) {
            if (rhs == null || getClass() != rhs.getClass())
                return false;
            RetInstruction other = (RetInstruction)rhs;
            return _flag == other._flag &&
                    _negated == other._negated;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_flag, _negated);
        }

        @Override
        public String toString() {
            return String.format("RET %s", computeFlagString(_flag, _negated));
        }

        @Override
        public void execute(EmulatorState state) {
            if (shouldJump(state, _flag, _negated))
                ret(state);
        }
    }

    public static class RetiInstruction implements Instruction {
        @Override
        public boolean equals(Object rhs) {
            return rhs != null && getClass() == rhs.getClass();
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public String toString() {
            return "RETI";
        }

        @Override
        public void execute(EmulatorState state) {
            state.interruptsEnabled = true;
            ret(state);
        }
    }
}
