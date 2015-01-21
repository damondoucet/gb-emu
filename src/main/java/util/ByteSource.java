package util;

/**
 * Interface for the ByteScanner so that we can use both a byte[] as well as
 * the emulator's memory state.
 */
public interface ByteSource {
    public int length();
    public byte get(int index);
}
