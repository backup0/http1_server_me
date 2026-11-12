package seditious.code.http.xtest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static seditious.code.http.xtest.Main.w;

public class XNio
{
    public void start()
    {
        try
        {
            start2();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void start2() throws Exception
    {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 80));

        SocketChannel sc = ssc.accept();
        ByteBuffer bbf = ByteBuffer.allocate(48);
        sc.configureBlocking(false);

        int i = 0;

        while (true)
        {
            bbf.clear();
            int qty = sc.read(bbf);
            if (qty > 0)
            {
                bbf.flip();

                while(bbf.hasRemaining())
                {
                    System.out.print((char) bbf.get()); // read 1 byte at a time
                }
            }
            else if (qty < 0)
            {
                break;
            }
            else
            {
                w("Sleep count -> ", i++);
                Thread.sleep(2500);
            }
            if (i >= 5)
            {
                w("Breakings ..");
                break;
            }
        }
    }

    /*
    You want to use select() <-- returns ok channel
    selectNow() <-- non blocking, so if none you get 0.
     */
}
