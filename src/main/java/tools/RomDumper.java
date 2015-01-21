package tools;

import cpu.disassembler.InstructionDecoder;
import cpu.disassembler.RootInstructionDecoder;
import memory.CartridgeHeader;
import util.ByteScanner;
import util.Util;
import static com.google.common.base.Preconditions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Prints the cartridge header as well as the entire ROM as if all bytes
 * (excluding the cartridge header) were instructions.
 */
public class RomDumper {
    private final static int HEADER_START = 0x104;
    private final static int HEADER_END = 0x14F;

    private byte[] _bytes;
    private ByteScanner _scanner;
    private InstructionDecoder _decoder;

    private RomDumper(byte[] bytes) {
        _bytes = bytes;
        _scanner = new ByteScanner(bytes);
        _decoder = new RootInstructionDecoder();
    }

    private void printHeader() {
        System.out.println(CartridgeHeader.parse(_bytes).toString());
        _scanner.seek(0);
    }

    // Create a String of the bytes between originalIndex and the scanner's
    // current position.
    private String getByteString(int originalIndex) {
        int finalIndex = _scanner.getIndex();

        _scanner.seek(originalIndex);
        String ret = "";

        while (_scanner.getIndex() < finalIndex)
            ret += Util.byteToHexStringWithoutPrefix(_scanner.readByte()) + " ";

        return ret;
    }

    private String readInstruction() {
        try {
            return _decoder.decodeNext(_scanner).toString();
        } catch (Exception e) {
            return "DB " + Util.byteToHexString(_scanner.readByte());
        }
    }

    private void dumpNextInstruction() {
        int start = _scanner.getIndex();

        String address = Util.shortToHexString((short)start);
        String instr = readInstruction();
        String bytes = getByteString(start);

        // The byte string will end with an extra space
        System.out.println(String.format("%s - %s- %s", address, bytes, instr));
    }

    private void dumpRom() {
        printHeader();

        while (_scanner.getIndex() < HEADER_START)
            dumpNextInstruction();

        System.out.println("    ...  Cartridge Header  ...");
        checkState(_scanner.getIndex() == HEADER_START);
        _scanner.seek(HEADER_END + 1);

        while (!_scanner.isEof())
            dumpNextInstruction();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Path?");

        String path = reader.readLine();
        byte[] bytes = Util.bytesFromFile(path);
        new RomDumper(bytes).dumpRom();
    }
}
