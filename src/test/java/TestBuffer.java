import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一 缓冲区(buffer)
 * 在java NIO中负责数据的存取.缓冲区就是数组,用于存储不同的数据类型的数据
 * <p>
 * 根据数据类型的不同(boolean除外),提供了相应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * <p>
 * 上述缓冲区的管理方式几乎一样,通过allocate() 获取缓冲区
 *
 * @author Zx
 * @date 2020/6/23 9:10
 * @modified By:
 */
public class TestBuffer {
    @Test
    public void test01() {
        String str = "abcde";

        //1. 分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("=================allocate()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //2. 利用put() 存入数据到缓冲区
        buf.put(str.getBytes());
        System.out.println("=================put()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //3. 利用flip() 切换到数据读取模式
        buf.flip();

        System.out.println("=================flip()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //.4 利用get() 读取缓冲区中的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst, 0, dst.length));

        System.out.println("=================get()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
    }

}
