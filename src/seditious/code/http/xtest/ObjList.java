/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import seditious.code.http.common.Headers;
import seditious.code.http.server.ExtraData;
import seditious.code.http.server.HTTPOut;
import seditious.code.http.server.HTTPRequest;
import seditious.code.http.server.PageHandler;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author user
 */
public class ObjList extends PageHandler
{
     public ObjList(HTTPRequest rqst)
     {
          super(rqst);
     }

     @Override
     public HTTPOut out(ExtraData exData)
     {
          StringBuilder sbs = new StringBuilder();
          sbs.append("<!DOCTYPE html>\n");
          sbs.append("<html>\n");
          sbs.append("<title>Super</title>");
          sbs.append("<body>");
          sbs.append(getStuff());
          sbs.append("</body>\n</html>");
          
          byte[] data = sbs.toString().getBytes(StandardCharsets.UTF_8);
          
          HTTPOut.HTTPOutBuilder builder = new HTTPOut.HTTPOutBuilder();
          builder.setOption(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
          builder.setOption("Access-Control-Allow-Origin", "*");
          builder.setOption(Headers.CONTENT_LENGTH, data.length + "");
          builder.setData(new ByteArrayInputStream(data));
          return builder.build();
     }
     
     private String getStuff()
     {
          StringBuilder sbs = new StringBuilder();
          ArrayList<String[]> data = getObjList();
          for (String[] s : data)
          {
               sbs.append("<div>");
               sbs.append("<a href=\"").append("/obj/").append(s[0]).append("\">");
               sbs.append(s[1]);
               sbs.append("</a>");
               sbs.append("</div>");
          }
          return sbs.toString();
     }
     
     private ArrayList<String[]> getObjList()
     {
          ArrayList<String[]> data = new ArrayList<>();
          
          String co = "select * from object";
          
          try (Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Download\\ermus\\s3;"
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
     
}
