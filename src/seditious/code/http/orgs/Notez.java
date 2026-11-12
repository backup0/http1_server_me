/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.orgs;

import java.io.Reader;
import java.io.StringReader;
import java.sql.*;

/**
 *
 * @author user
 */
public class Notez
{
    public static void createData(String s)
    {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Download\\ermus\\smv3;"
                + "TRACE_LEVEL_SYSTEM_OUT=1;TRACE_LEVEL_FILE=0;", "sa", ""))
        {
            final Long oid = createObj(conn);
            if (oid == null) throw new SQLException();

            final Long dataId = storeData(conn, oid, s);
            if (oid == null) throw new SQLException();

            updateVersion(conn, oid, dataId);
        }
        catch (SQLException ex)
        {
            //return data;
            ex.printStackTrace();
        }
    }

    private static Long createObj(Connection conn) throws SQLException
    {
        final String pss = "insert into object(deleted, current) values(false, null)";
        try (PreparedStatement ps = conn.prepareStatement(pss))
        {
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys())
            {
                if (rs.first())
                {
                    return rs.getLong(1);
                }
                else
                {
                    return null;
                }
            }
        }
    }

    private static Long storeData(Connection conn, Long id, String s) throws SQLException
    {
        final String pss = "insert into data(oid, data, version, deleted) values(?, ?, 0, false)";
        try (PreparedStatement ps = conn.prepareStatement(pss))
        {
            ps.setLong(1, id);
            ps.setCharacterStream(2, new StringReader(s));
            ps.execute();

            try (ResultSet rs = ps.getGeneratedKeys())
            {
                if (rs.first())
                {
                    return rs.getLong(1);
                }
                else
                {
                    return null;
                }
            }
        }
    }

    private static void updateVersion(Connection conn, Long oid, Long dataId) throws SQLException
    {
        final String pss = "update object set current = ? where id = ?";
        try (PreparedStatement ps = conn.prepareStatement(pss))
        {
            ps.setLong(1, dataId);
            ps.setLong(2, oid);
            ps.execute();
        }
    }
}
