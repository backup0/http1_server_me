package seditious.code.http.server;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ContentProcessor
{

    private final ArrayDeque<HT1Connection> ard;
    private final ArrayList<HT1Connection> procQueu = new ArrayList<>();

    public ContentProcessor(ArrayDeque<HT1Connection> ard)
    {
        this.ard = ard;
        run();
    }

    private void run()
    {
        final var th000 = new Thread(() ->
        {
            while (true)
            {
                final HT1Connection conn;
                synchronized (procQueu)
                {
                    if (procQueu.isEmpty())
                    {
                        try
                        {
                            procQueu.wait();
                        } catch (Exception ignore){}
                        continue;
                    }
                    else
                    {
                        conn = procQueu.remove(procQueu.size() - 1);
                    }
                }

                conn.doSomething();
                final var stat = conn.getWhatToDoNext();
                if (stat == HT1Connection.Stat.CLOSE)
                {
                    conn.closeSocket();
                    continue;
                }

                synchronized (ard)
                {
                    ard.add(conn);
                    ard.notify();
                }
            }
        });
        th000.setDaemon(true);
        th000.start();
    }

    public void addStuff(HT1Connection conn)
    {
        synchronized (procQueu)
        {
            procQueu.add(conn);
            procQueu.notify();
        }
    }
}
