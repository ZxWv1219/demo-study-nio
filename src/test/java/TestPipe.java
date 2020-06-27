import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @author Zx
 * @date 2020/6/27 18:06
 * @modified By:
 */
public class TestPipe {
    @Test
    public void test1() throws IOException {
        Pipe pipe = Pipe.open();
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //将缓冲区中的数据写入通道中
        Pipe.SinkChannel sinkChannel = pipe.sink();
        buf.put("通过单向通道发送数据".getBytes());
        buf.flip();
        sinkChannel.write(buf);

        //从通道中读取缓冲区数据
        Pipe.SourceChannel sourceChannel = pipe.source();
        buf.flip();
        int len = sourceChannel.read(buf);
        System.out.println(new String(buf.array(), 0, len));

        sinkChannel.close();
        sourceChannel.close();
    }
}
