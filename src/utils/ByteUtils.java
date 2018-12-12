package utils;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 11/09/18
 * Time: 21.57
 */
public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public synchronized static byte[] longToBytes(long x) {
        buffer.clear();
        buffer.putLong(0, x);
        return buffer.array();
    }

    public synchronized static long bytesToLong(byte[] bytes) {
        buffer.clear();
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}