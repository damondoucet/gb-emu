package util;

/**
 * Wraps a byte[] in a ByteSource object for the ByteScanner.
 */
public class ArrayByteSource implements ByteSource {
    private final byte[] _bytes;

    public ArrayByteSource(byte[] bytes) {
        _bytes = bytes;
    }

    @Override
    public int length() {
        return _bytes.length;
    }

    @Override
    public byte get(int index) {
        return _bytes[index];
    }
}
