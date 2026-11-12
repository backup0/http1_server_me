/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import seditious.code.http.common.Headers;
import seditious.code.http.server.ExtraData;
import seditious.code.http.server.HTTPOut;
import seditious.code.http.server.HTTPRequest;
import seditious.code.http.server.PageHandler;

/**
 *
 * @author user
 */
public class Objs extends PageHandler
{

     public Objs(HTTPRequest rqst)
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
          sbs.append(getData(request.getPath().get(1)));
          sbs.append("<p>");
          sbs.append("<a href=\"list\">");
          sbs.append("&lt;-- Go back");
          sbs.append("</a>");
          sbs.append("</p>");
          sbs.append("</body>\n</html>");
          
          byte[] data = sbs.toString().getBytes(StandardCharsets.UTF_8);
          
          HTTPOut.HTTPOutBuilder builder = new HTTPOut.HTTPOutBuilder();
          builder.setOption(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
          builder.setOption("Access-Control-Allow-Origin", "*");
          builder.setOption(Headers.CONTENT_LENGTH, data.length + "");
          builder.setData(new ByteArrayInputStream(data));
          return builder.build();
     }
     
     private String getData(String id)
     {
          String co = "select * from object where oid = ?";
          String cs = "select * from d_text where oid = ? and version <= ? and deleted is false";
          
          try (Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Download\\ermus\\s3;"
                          + "TRACE_LEVEL_SYSTEM_OUT=1;TRACE_LEVEL_FILE=0;", "sa", "");
                  PreparedStatement pObj = conn.prepareStatement(co);
                  PreparedStatement pDat = conn.prepareStatement(cs))
          {               
               StringBuilder theObj = new StringBuilder();
               StringBuilder theVal = new StringBuilder();
               
               int version = 0;
               pObj.setObject(1, id);
               try (ResultSet rs = pObj.executeQuery())
               {
                    if (rs.first())
                    {
                         UUID oid = (UUID) rs.getObject("oid");
                         String name = rs.getString("name");
                         version = rs.getInt("version"); 
                         String note = rs.getString("note");
                         long domain = rs.getLong("domain");
                         
                         theObj.append("<h1>").append(name);
                            theObj.append(" <sub style=\"font-weight:normal; color: gray; font-size:small\">");
                                theObj.append(oid.toString())
                                        .append(": ")
                                        .append(version);
                            theObj.append("</sub>");
                         theObj.append("</h1>");                      
                    }
               }
               
               // the fields ...               
               pDat.setObject(1, id);
               pDat.setInt(2, version);
               try (ResultSet rs = pDat.executeQuery())
               {
                    theVal.append("<p>");
                    while (rs.next())
                    {             
                         
                         theVal.append(rs.getString("name"));
                         theVal.append(": ");
                         theVal.append(rs.getString("value"));
                                                  
                         theVal.append("<br>");
                         
                    } 
                    theVal.append("</p>");
               }     
               return theObj.toString() + "\n\n\n" + theVal.toString();
          }          
          catch (SQLException ex)
          {
               return "Oooops!!";
          }  
     }
     
}
