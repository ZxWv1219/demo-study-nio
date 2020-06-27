import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一 使用NIO完成网络通信的三个核心
 * <p>
 * 1. 通道(channel): 负责连接
 * java.nio.channels.Channel 接口:
 * |--SelectableChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * |--Pipe.SinkChannel
 * |--Pipe.SourceChannel
 * <p>
 * 2. 缓冲菜(buffer): 负责数据的存取
 * <p>
 * 3. 选择器(selector): 是selectableChannel 的多路复用器. 用于监控selectableChannel 的IO状况
 *
 * @author Zx
 * @date 2020/6/27 11:57
 * @modified By:
 */
public class TestBlockingNIO {
    @Test
    public void client() throws IOException {
        //1. 获取通道
        SocketChannel socketChannel =
                SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);

        //2. 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //3. 读取本地文件,发送到服务端
        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //4. 关闭通道
        fileChannel.close();
        socketChannel.close();

    }

    @Test
    public void server() throws IOException {
        //1. 获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        FileChannel fileChannel = FileChannel.open(Paths.get("3.jpg"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //2. 绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));

        //3. 获取客户端连接的通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        //4. 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //5. 读取客户端数据并保存到本地
        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        serverSocketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();
    }
}
