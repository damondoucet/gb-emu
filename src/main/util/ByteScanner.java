package main.util;

import java.util.Arrays;
import static com.google.common.base.Preconditions.*;

/**
 * Utility class for scanning through an in-memory array of bytes like a file.
 */
public class ByteScanner {
    private int _index;
    private final byte[] _bytes;

    public ByteScanner(byte[] bytes) {
        checkArgument(bytes.length > 0);
        _index = 0;
        _bytes = bytes;
    }

    public void seek(int index) {
        _index = index;

        checkState(_index >= 0);
        checkState(_index < _bytes.length);
    }

    public byte peek() {
        return peek(0);
    }

    public byte peek(int index) {
        checkState(_index >= 0);
        checkArgument(_index + index < _bytes.length);

        return _bytes[_index + index];
    }

    public byte readByte() {
        checkState(_index >= 0);
        checkState(_index < _bytes.length);

        byte ret = _bytes[_index];
        _index++;
        return ret;
    }

    public byte[] readBytes(int length) {
        checkState(_index >= 0);
        checkArgument(_index + length - 1 < _bytes.length);

        byte[] ret = Arrays.copyOfRange(_bytes, _index, _index + length);
        _index += length;
        return ret;
    }
}
