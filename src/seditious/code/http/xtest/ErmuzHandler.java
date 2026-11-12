/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import seditious.code.http.common.Headers;
import seditious.code.http.server.ExtraData;
import seditious.code.http.server.HTTPOut;
import seditious.code.http.server.HTTPOut.HTTPOutBuilder;
import seditious.code.http.server.HTTPRequest;
import seditious.code.http.server.PageHandler;
import seditious.code.http.server.RequestHandler;

/**
 *
 * @author user
 */
public class ErmuzHandler implements RequestHandler
{

     @Override
     public PageHandler yo(HTTPRequest rqst)
     {
          return new ErmusPage(rqst);
     }
     
     private static class ErmusPage extends PageHandler
     {
          private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          
          public ErmusPage(HTTPRequest rqst)
          {
               super(rqst);
          }

          @Override
          public OutputStream in()
          {
               return bos;
          }
          
          @Override
          public void doneIn(OutputStream os, boolean ok)
          {               
               //System.out.println(bos.toString());
          }
          
          @Override
          public HTTPOut out(ExtraData exData)
          {
               //byte[] data = ("This is shites..." + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
               final byte[] data;
               if (bos.toByteArray().length == 0)
               {
                    //data = ("This is MT" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
                    StringBuilder sbs = new StringBuilder();
                    sbs.append("<!DOCTYPE html>\n");
                    sbs.append("<html>\n");
                    sbs.append("<title>Super</title>");
                    sbs.append("<body>");
                    sbs.append("<h1>In nothing should we trust .. </h1>");
                    sbs.append("<p>Trust me - we're a'ok</p>");
                    sbs.append("<p>Part 3 -- neh!</p>");
                    sbs.append("<p>+-, interstation .. Lore ipsum, ipsicum epiteth .. Neh!</p>");
                    sbs.append("<p><i>").append(System.currentTimeMillis()).append("</i></p>");
                    sbs.append("<p>");
                    sbs.append("some paragraph to eb &copy; ritten out of <b>blood</b>. And mucus..<br>");
                    sbs.append("Why is this wrong, it isn't shapposed to be wrong. Why .. oh whys? ");
                    sbs.append("Dawnt knwos man, dawnt knows -- fuck you. Fukc you!!");
                    sbs.append("</p>");
                    sbs.append("<p>My book is yours to .. yeah, that's true.</p>");
                    sbs.append("<p><i>@ :> ").append(request.getPath()).append("</i></p>");
                    sbs.append("</body>\n</html>");
                    data = sbs.toString().getBytes(StandardCharsets.UTF_8);
               }
               else
               {
                    data = bos.toByteArray();
               }
               
               HTTPOutBuilder builder = new HTTPOut.HTTPOutBuilder();
               builder.setOption(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
               builder.setOption("Access-Control-Allow-Origin", "*");
               builder.setOption(Headers.CONTENT_LENGTH, data.length + "");
               builder.setData(new ByteArrayInputStream(data));
               return builder.build();
               
               
          }
     }
}
