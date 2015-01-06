package test.util;

import main.util.ByteScanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

public class ByteScannerTest {
    private ByteScanner _scanner;

    @Before
    public void initialize() {
        _scanner = new ByteScanner(new byte[] { 1, 2, 3 });
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRead() {
        Assert.assertEquals(_scanner.readByte(), 1);
        Assert.assertEquals(_scanner.readByte(), 2);
        Assert.assertEquals(_scanner.readByte(), 3);

        thrown.expect(IllegalStateException.class);
        _scanner.readByte();
    }

    @Test
    public void testSeek() {
        Assert.assertEquals(_scanner.readByte(), 1);
        _scanner.seek(0);
        Assert.assertEquals(_scanner.readByte(), 1);
        Assert.assertEquals(_scanner.readByte(), 2);
        Assert.assertEquals(_scanner.readByte(), 3);

        _scanner.seek(1);
        Assert.assertEquals(_scanner.readByte(), 2);
    }

    @Test
    public void testReadBytes() {
        Assert.assertArrayEquals(_scanner.readBytes(2), new byte[] { 1, 2 });
        Assert.assertArrayEquals(_scanner.readBytes(1), new byte[] { 3 });

        thrown.expect(IllegalArgumentException.class);
        _scanner.readBytes(2);

        _scanner.seek(2);
        thrown.expect(IllegalArgumentException.class);
        _scanner.readBytes(2);
    }

    @Test
    public void testPeek() {
        Assert.assertEquals(_scanner.peek(), 1);

        // Peeking shouldn't change the index
        Assert.assertEquals(_scanner.peek(), 1);

        Assert.assertEquals(_scanner.peek(0), 1);
        Assert.assertEquals(_scanner.peek(1), 2);
        Assert.assertEquals(_scanner.peek(2), 3);

        thrown.expect(IllegalArgumentException.class);
        _scanner.peek(3);

        thrown.expect(IllegalArgumentException.class);
        _scanner.peek(-1);
    }

    @Test
    public void testPeekAfterRead() {
        Assert.assertEquals(_scanner.readByte(), 1);

        Assert.assertEquals(_scanner.peek(-1), 1);
        Assert.assertEquals(_scanner.peek(0), 2);
        Assert.assertEquals(_scanner.peek(1), 3);

        thrown.expect(IllegalArgumentException.class);
        _scanner.peek(2);

        thrown.expect(IllegalArgumentException.class);
        _scanner.peek(-2);
    }
}