package cpu.disassembler.instructions;

import cpu.EmulatorState;
import cpu.disassembler.Instruction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Instructions for controlling the CPU:
 *      HALT, STOP,
 *      EI, DI
 */
public class CpuControlInstructions {
    public static class EiInstruction extends Instruction {
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
            return "EI";
        }

        @Override
        public void execute(EmulatorState state) {
            state.interruptsEnabled = true;
        }
    }

    public static class DiInstruction extends Instruction {
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
            return "DI";
        }

        @Override
        public void execute(EmulatorState state) {
            state.interruptsEnabled = false;
        }
    }

    public static class HaltInstruction extends Instruction {
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
            return "HALT";
        }

        @Override
        public void execute(EmulatorState state) {
            state.halt();
        }
    }

    public static class StopInstruction extends Instruction {
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
            return "STOP";
        }

        @Override
        public void execute(EmulatorState state) {
            // We'll cross this bridge if any ROMs actually need this instr...
            throw new NotImplementedException();
        }
    }
}
