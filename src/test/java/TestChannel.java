import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * 一 通道(channel) ： 用于源节点与目标节点的连接。在java NIO中负责缓冲区中数据的传输。因此需要配合缓冲区进行传输
 * <p>
 * 二 通道的主要实现类
 * java.nio.channels.Channel 接口：
 * |--FileChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * 三 获取通道
 * 1.Java 针对支持通道的类提供了 getChannel()方法
 * 本地IO :
 * FileInputStream/FileOutStream
 * RandomAccessFile
 * <p>
 * 网络IO :
 * Socket
 * ServerSocket
 * DataGramSocket
 * <p>
 * 2. 在JDK 1.7 中的NIO.2 针对各个通道提供了静态方法open()
 * 3. 在JDK 1.7 中的NIO.2 的Files 工具类的 newByteChannel()
 * <p>
 * 四 通道之间的数据传输
 * transferFrom()
 * transferTo()
 * <p>
 * 五 分散(Scatter)与聚集(Gather)
 * 分散读取(Scattering Reads): 将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gathering Writes): 将多个缓冲区中的数据聚集到通道中
 * <p>
 * 六 字符集 Charset
 * 编码:字符串转成字节数组
 * 解码: 字节数组转成字符串
 *
 * @author Zx
 * @date 2020/6/23 15:27
 * @modified By:
 */
public class TestChannel {

    //非直接缓冲区 复制文件
    @Test
    public void testCopy() {
        FileInputStream fis = null;
        FileOutputStream fos = null;


        //1. 获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            //利用通道完成文件的复制
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");


            //1. 获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2. 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3. 将通道中的数据存入缓冲区中
            while (inChannel.read(buf) != -1) {
                //切换读取数据模式
                buf.flip();
                //4. 将缓冲区中的数据写入通道中
                outChannel.write(buf);
                //清空缓冲区
                buf.clear();
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //直接缓冲区 复制文件
    @Test
    public void testCopy1() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区数据进行读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();
    }

    //直接缓冲区(通道之间的数据传输) 复制文件
    @Test
    public void testTransfer() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChannel.close();
    }

    //分散和聚集
    @Test
    public void test4() throws IOException {
        T i = new T();
        RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");

        //1. 获取通道
        FileChannel channelRead = raf1.getChannel();
        //2. 分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        //3. 分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        channelRead.read(bufs);

        for (ByteBuffer buffer : bufs) {
            buffer.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("我是分割线------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //4. 聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel channelWrite = raf2.getChannel();

        channelWrite.write(bufs);
    }

    //字符集
    @Test
    public void test5() throws CharacterCodingException {
        Charset charset1 = Charset.forName("GBK");

        //获取编码器
        CharsetEncoder charsetEncoder = charset1.newEncoder();
        //获取解码器
        CharsetDecoder charsetDecoder = charset1.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("我是Zx");
        charBuffer.flip();

        //编码
        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);

        for (int i = 0; i < 6; i++) {
            System.out.println(byteBuffer.get());
        }
        byteBuffer.flip();
        CharBuffer charBuffer1 = charsetDecoder.decode(byteBuffer);

//        charBuffer1.flip();
        System.out.println(charBuffer1.toString());


        Charset charset = StandardCharsets.UTF_8;
        byteBuffer.flip();
        CharBuffer charBuffer2 = charset.decode(byteBuffer);
        System.out.println(charBuffer2.toString());
    }
}
