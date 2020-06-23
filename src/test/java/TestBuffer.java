import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.omg.CORBA.PUBLIC_MEMBER;

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
 * <p>
 * 二 缓冲区存取数据的两个核心方法
 * put()：存入数据到缓冲区
 * get()：获取缓冲区中的数据
 * <p>
 * 三 缓冲区中的四个核心属性
 * capacity : 容量，表示缓冲区中最大存储数据的容量，一旦声明不能修改
 * limit : 界限，表示缓冲区中可以操作的数据大小，（limit 后数据不能进行读写）
 * position : 位置，表示缓冲区中下在操作数据的位置
 * <p>
 * mark ： 用于记录标记当前position的位置， 可以通过reset() 恢复到mark标记的位置
 * <p>
 * 0<= mark <= position <= limit <= capacity
 * <p>
 * 四 直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过allocate()方法分配的缓冲区，他是将缓冲区建立在JVM的内存中
 * 直接缓冲区：通过allocateDirect()方法分配的缓冲区，将缓冲区建立在物理内存中，可提高效率
 *
 * @author Zx
 * @date 2020/6/23 9:10
 * @modified By:
 */
public class TestBuffer {

    @Test
    public void test01() {
        String str = "abcde";

        //1. 分配一个指定大小的缓冲区，非直接缓冲区
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

        //5. rewind倒带 可重复读数据
        buf.rewind();

        System.out.println("=================rewind()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //6. 清空缓冲区 clear(),但是缓冲区中的数据依然存在，处理补遗忘状态
        buf.clear();
        System.out.println("=================clear()=================");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

    }

    @Test
    public void test2() {
        String str = "abcde";

        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.put(str.getBytes());

        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst, 0, 2);
        System.out.println(new String(dst, 0, 2));
        System.out.println(buf.position());

        buf.mark();
        buf.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));
        System.out.println(buf.position());

        //恢复到mark的position
        buf.reset();
        System.out.println(buf.position());

        //判断缓冲区中是否有剩余的数据
        if (buf.hasRemaining()) {
            System.out.println(buf.remaining());
        }
    }

    @Test
    public void test03() {
        //分配直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        System.out.println(buf.isDirect());
    }

}
