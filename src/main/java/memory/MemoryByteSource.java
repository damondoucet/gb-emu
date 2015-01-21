package memory;

import util.ByteSource;

/**
 * ByteSource that wraps a Memory object.
 */
public class MemoryByteSource implements ByteSource {
    private final Memory _memory;

    public MemoryByteSource(Memory memory) {
        _memory = memory;
    }

    @Override
    public int length() {
        return 0xFFFF;
    }

    @Override
    public byte get(int index) {
        return _memory.readByte((short)index);
    }
}
