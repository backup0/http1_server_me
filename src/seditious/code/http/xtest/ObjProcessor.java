package seditious.code.http.xtest;

import seditious.code.http.common.Headers;
import seditious.code.http.server.ExtraData;
import seditious.code.http.server.HTTPOut;
import seditious.code.http.server.HTTPRequest;
import seditious.code.http.server.PageHandler;
import seditious.code.json.JSON;
import seditious.code.json.ParseException;

import java.io.*;
import java.sql.*;

import static seditious.code.http.xtest.Main.w;

public class ObjProcessor extends PageHandler
{
    private HTTPRequest _rqst;
    private ByteArrayOutputStream _bos;

    /**
     * @param rqst HTTPRequest instance.
     */
    public ObjProcessor(HTTPRequest rqst)
    {
        super(rqst);
        _bos = new ByteArrayOutputStream();
    }

    public OutputStream in()
    {
        return _bos;
    }

    @Override
    public HTTPOut out(ExtraData exData)
    {
        return getSomeData();
    }

    private HTTPOut getSomeData()
    {
        HTTPOut.HTTPOutBuilder bldr = new HTTPOut.HTTPOutBuilder();
        bldr.setOption(Headers.CONTENT_TYPE, "text/plain; charsets=utf-8");
        final byte[] b = getContent();
        bldr.setOption(Headers.CONTENT_LENGTH, b.length + "");
        bldr.setData(new ByteArrayInputStream(b));
        return bldr.build();
    }

    private byte[] getContent()
    {
        try
        {
            JSON j = JSON.create(_bos.toString());
            System.out.println(j.get("oid"));

            Object[] oz = j.getArray("fields");
            for (Object o : oz)
            {
                w(o.getClass().getName());
            }
            //shite();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return _bos.toByteArray();
    }

    public void shite(String s) throws ParseException, IOException
    {
        _bos.write(s.getBytes());
        shite();
    }

    public void shite() throws ParseException
    {
        final JSON j = JSON.create(_bos.toString());
        final JSON jData = j.getJSON("data");

        try (Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Download\\ermus\\s40;"
                + "TRACE_LEVEL_SYSTEM_OUT=1;TRACE_LEVEL_FILE=0;", "sa", ""))
        {
            if (isNew(j))
            {
                final String name = j.getString("name");
                createObject(conn, name, "");
            }
            else
            {
                final Long id = Long.valueOf(j.getString("oid"));
                pushData(conn, id, jData.toString());
            }
        }
        catch (SQLException ex)
        {
            //return data;
            ex.printStackTrace();
        }
    }

    // ok if it;s new then create a new one then push a new data on
    // else

    private void createObject(Connection con, String name, String note) throws SQLException
    {
        note = (note == null) ? "" : note;

        final String psS = "insert into object(name, note) values(?, ?)";
        try (PreparedStatement ps = con.prepareStatement(psS))
        {
            ps.setString(1, name);
            ps.setString(2, note);

            ps.executeUpdate();

            try (ResultSet genKey = ps.getGeneratedKeys())
            {
                if (genKey.first())
                {
                    pushData(con, genKey.getLong(1), _bos.toString());
                }
            }
            catch (SQLException ex)
            {
                throw ex;
            }
        }
    }

    private void pushData(Connection con, long id, String data)
    {
        final String psS = "insert into vals(oid, version, val, deleted) values(?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(psS))
        {
            ps.setLong(1, id);
            ps.setInt(2, getVersion(con, id));
            ps.setCharacterStream(3, new StringReader(data));
            ps.setBoolean(4, false);
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private int getVersion(Connection con, long id) throws SQLException
    {
        final String ss = "select max(version) from vals where oid = ?";

        try (PreparedStatement ps = con.prepareStatement(ss))
        {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                int version = 0;

                if (rs.first())
                {
                    int v = rs.getInt(1);
                    version = v + 1;
                    w(v);
                }

                if (rs.wasNull())
                {
                    w("the value was null");
                    version = 0;
                }

                return version;
            }
            catch (SQLException e)
            {
                throw e;
            }
        }
    }

    private boolean isNew(JSON j)
    {
        final String s = j.getString("oid");

        if (s == null) // what if id does not exist?
        {
            return true;
        }
        else
        {
            try
            {
                Long.valueOf(s);
                return false;
            }
            catch (NumberFormatException nfe)
            {
                return true;
            }
        }
    }

    /*
    private void pushData()
    {
    // PreparedStatement.setCharacterStream
        try (Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Download\\ermus\\s40;"
                + "TRACE_LEVEL_SYSTEM_OUT=1;TRACE_LEVEL_FILE=0;", "sa", "");
             PreparedStatement pGet = conn.prepareStatement(co))
        {

            try (ResultSet rs = pGet.executeQuery())
            {
                while (rs.next())
                {
                    String[] ss = new String[]{"", ""};

                    UUID id = (UUID) rs.getObject("oid");
                    String name = rs.getString("name");
                    ss[0] = id.toString();
                    ss[1] = name;
                    data.add(ss);
                }
            }
            return data;
        }
        catch (SQLException ex)
        {
            return data;
        }
    }
    */
}
