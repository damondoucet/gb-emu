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

    private void dumpNextInstruction() {
        System.out.println(Util.readNextInstruction(_decoder, _scanner));
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
