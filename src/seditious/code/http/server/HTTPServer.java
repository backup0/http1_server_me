package seditious.code.http.server;

import seditious.code.http.Main;
import seditious.code.http.exception.ServerException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.LinkedList;

/**
 * @author user
 */
public class HTTPServer {

    private final LinkedList<HT1Connection> INQueu = new LinkedList<>();
    private final ArrayDeque<HT1Connection> OUTQueu = new ArrayDeque <>();

    private final ServerSettings settings = new ServerSettings();

    private final int svrAcceptTimeout = 5000;

    boolean endServer = false;

    private Thread secureServerThread;
    private Thread insecureServerThread;
    // process data in and out
    private Thread INProcThread;
    private Thread OUTProcThread;
    // kill the server gently.
    private Thread svrKillerThread;

    private ContentProcessor[] procs;

    public ServerSettings getSettings() {
        return settings;
    }

    public void startServer() throws ServerException {

        if (settings.getAddress() == null) {
            throw new ServerException("No address");
        }

        startRequestProcessor();
        final String[] keystoreInfo = settings.getKeyStore();
        if (keystoreInfo.length == 2) {
            System.setProperty("javax.net.ssl.keyStore", keystoreInfo[0]);
            System.setProperty("javax.net.ssl.keyStorePassword", keystoreInfo[1]);
            startSecureServer();
        }
        startNonSecureServer();
    }

    public void killServer() {
        endServer = true;
    }

    private void startSecureServer() {

        secureServerThread = new Thread(() -> {
            final ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();

            try (ServerSocket socket = ssf.createServerSocket()) {

                final InetAddress address = settings.getAddress();
                final int port = settings.getPorts()[1];
                socket.bind(new InetSocketAddress(address, port));
                socket.setSoTimeout(svrAcceptTimeout);
                // now read and write from the socket ...
                while (!endServer)
                // loop waiting for connection.
                // switch cont to false - server dies
                {
                    try {
                        final Socket clientSocket = socket.accept();
                        queuConnection(clientSocket);
                    }
                    catch (Exception ignored) {}
                }
                closeServerGracefully();
            }
            catch (Exception ex) {
                if (insecureServerThread != null) {
                    insecureServerThread.interrupt();
                }
                settings.getLogger().log(ex.getMessage());
            }
        });
        secureServerThread.start();
    }

    private void startNonSecureServer() {

        insecureServerThread  = new Thread(() -> {
            try (ServerSocket socket = new ServerSocket()) {

                final InetAddress address = settings.getAddress();
                final int port = settings.getPorts()[0];
                socket.bind(new InetSocketAddress(address, port));
                socket.setSoTimeout(svrAcceptTimeout);

                // now read and write from the sokket ...
                while (!endServer)
                // loop waiting for connection.
                // switch cont to false - server dies
                {
                    try
                    {
                        final Socket clientSocket = socket.accept();
                        queuConnection(clientSocket);
                    }
                    catch (IOException ignored) {}
                }
                closeServerGracefully();
            }
            catch (Exception ex) {
                if (secureServerThread != null) {
                    secureServerThread.interrupt();
                }
                settings.getLogger().log(ex.getMessage());
            }
        });
        insecureServerThread.start();
    }

    private synchronized void closeServerGracefully() {

        // already started the killing thread.
        if (svrKillerThread != null) {
            return;
        }

        svrKillerThread = new Thread(() -> {

            // this in order to avoid race condition.
            try {
                Thread.sleep(svrAcceptTimeout * 2);
            } catch (InterruptedException ignored) {}

            while(true) {
                synchronized (INQueu) {
                    // no more conn to queue.
                    // and IO processing thread has gone to sleep, done.
                    if (INQueu.isEmpty() && INProcThread.getState() == Thread.State.WAITING) {
                        break;
                    }
                    else {
                        try {
                            Thread.sleep(1000);
                        }
                        catch (Exception ignored) {}
                    }
                }
            }
            settings.notifyAboutClosing();
        });
    }

    private void queuConnection(Socket skt) throws Exception {

        final HT1Connection con = new HT1Connection(settings, skt);
        synchronized (INQueu) {
            // add into q
            INQueu.add(con);
            // wake up thread in case it is still napping.
            INQueu.notify();
        }
    }

    int index = 0;
    private void startRequestProcessor()
    {

        procs = new ContentProcessor[]{new ContentProcessor(OUTQueu), new ContentProcessor(OUTQueu),
                new ContentProcessor(OUTQueu)};

        OUTProcThread = new Thread(this::handleWritingStuff);
        OUTProcThread.setDaemon(true);
        OUTProcThread.start();

        INProcThread = new Thread(this::handleReadingStuff);
        INProcThread.setDaemon(true);
        INProcThread.start();
    }

    private void handleReadingStuff()
    {
        boolean noAction = true;
        int i = 0;

        while (true)
        {
            final HT1Connection conn;
            synchronized (INQueu)
            {
                if (INQueu.isEmpty())
                {
                    Main.w("Long ass wait ...");
                    // empty queue; nothing to do ..
                    try
                    {
                        INQueu.wait();
                    }
                    catch (InterruptedException ignored) {}
                    i = 0; // it's empty -- so restart @ start.
                    continue;
                }
                conn = INQueu.get(i);
            }

            final HT1Connection.Stat i0 = conn.getWhatToDoNext();

            if (i0 == HT1Connection.Stat.READ_HEADER)
            {
                noAction = !conn.doSomething();
                i++;
            }
            else
            {
                synchronized (INQueu)
                {
                    INQueu.remove(i);
                }

                if (i0 == HT1Connection.Stat.PROCESS_RESPONSE)
                {
                    if (index >= procs.length)
                    {
                        index = 0;
                    }
                    procs[index++].addStuff(conn);
                }
                else
                {
                    conn.closeSocket();
                }
            }

            synchronized (INQueu)
            {
                if (i < INQueu.size())
                {
                    continue;
                }

                if (noAction)
                {
                    try
                    {
                        INQueu.wait(settings.getSleepTimeout());
                        //INQueu.wait(125);
                    }
                    catch (Exception ignore) {}
                }
                i = 0;
            }
        }
    }

    private void handleWritingStuff() {

        while (true)
        {

            final HT1Connection conn;
            synchronized (OUTQueu)
            {
                if (OUTQueu.isEmpty())
                {
                    Main.w("Long ass wait ...");
                    // empty queue; nothing to do ..
                    try
                    {
                        OUTQueu.wait();
                    }
                    catch (InterruptedException ignored){}
                    continue;
                }
                conn = OUTQueu.removeLast();
            }

            // ask ?? ?? what we should do next
            final HT1Connection.Stat i0 = conn.getWhatToDoNext();
            if (i0 == HT1Connection.Stat.WRITE_OUT)
            {
                conn.doSomething();
                // write
                synchronized (OUTQueu)
                {
                    OUTQueu.addFirst(conn);
                }
            }
            else if (i0 == HT1Connection.Stat.READ_HEADER)
            {
                // send back to process queu.
                synchronized (INQueu)
                {
                    INQueu.add(conn);
                    INQueu.notify();
                }
            }
            else
            {
                conn.closeSocket();
            }
        }
    }
}