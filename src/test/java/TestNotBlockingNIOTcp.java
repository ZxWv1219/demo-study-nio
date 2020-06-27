import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author Zx
 * @date 2020/6/27 16:20
 * @modified By:
 */
public class TestNotBlockingNIOTcp {
    @Test
    public void client() throws IOException {
        //1. 获取通道
        SocketChannel socketChannel =
                SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //2. 切换非阻塞模式
        socketChannel.configureBlocking(false);
        //3. 分配指定大小缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.put("开启客户端".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();

//        Scanner scanner = new Scanner(System.in);
//
//        while (scanner.hasNext()) {
//            String str = scanner.next();
//            //4. 发送数据给服务端
//            byteBuffer.put((new Date().toString() + "\n" + str).getBytes());
//            byteBuffer.flip();
//            socketChannel.write(byteBuffer);
//            byteBuffer.clear();
//        }


        //关闭通道
        socketChannel.close();
    }

    @Test
    public void server() throws IOException {
        //1. 获取通道
        ServerSocketChannel serverSocketChannel
                = ServerSocketChannel.open();
        //2. 切换到非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //3. 绑定通道
        serverSocketChannel.bind(new InetSocketAddress(9898));
        //4. 获取选择器
        Selector selector = Selector.open();
        //5. 将通道注册到选择器上,并且指定"监听事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6. 轮询式的获取选择器上已经"准备就绪"的事件
        while (selector.select() > 0) {
            //7. 获取当前选择器中所有注册的"选择键(已就绪的监听事件)"
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                //8. 获取准备就绪的事件
                SelectionKey key = iterator.next();
                //9. 判断具体事件类型
                if (key.isAcceptable()) {
                    //10. 若接收就绪,则获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //11. 切换客户端为非阻塞模式
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    //13. 获取读取就绪通道
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    //14. 读取客户端数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = socketChannel.read(byteBuffer)) != -1) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }
                //15. 取消选择建
                iterator.remove();
            }
        }


    }
}
