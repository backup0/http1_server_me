/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.HeaderEntry;
import seditious.code.http.common.HeaderList;
import seditious.code.http.common.URLPath;
import seditious.code.http.server.ErrorResponse;
import seditious.code.http.server.ExtraData;
import seditious.code.http.server.FileEntry;
import seditious.code.http.server.FileGateKeeper;
import seditious.code.http.server.FileList;
import seditious.code.http.server.GETEntry;
import seditious.code.http.server.GETList;
import seditious.code.http.server.HTTPOut;
import seditious.code.http.server.HTTPRequest;
import seditious.code.http.server.POSTEntry;
import seditious.code.http.server.POSTList;
import seditious.code.http.server.Page;
import seditious.code.http.server.PageHandler;
import seditious.code.http.server.RequestHandler;
import static seditious.code.http.xtest.Main.w;

/**
 *
 * @author user
 */
public class Handlers
{
     public static class ePHbos extends PageHandler
     {
          public ePHbos(HTTPRequest rqst)
          {
               super(rqst);
          }

          private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
          public OutputStream in()
          {
               /*
               int shit[] = new int[]{};
               int o = shit[49];
               */
               return bos;
          }
          
          public void doneIn(OutputStream os, boolean f)
          {
               w("Success?? ", f, os.toString());
               try
               {
                    super.doneIn(os, f);
               }
               catch (Exception ex)
               {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
               }
               //w("Pozzted Data --- \n", bos.toString());               
          }
          
          @Override
          public HTTPOut out(ExtraData ed)
          {              
               final Page page = new Page();
               page.setResponseLine(new HTTPStatus.HTTP200());

               page.w("<!DOCTYPE html>");
               page.w("<html>");
               page.w("<head>");
               page.w("<meta charset=\"UTF-8\">");
               page.w("<title>M'kayz</title>");
               page.w("</head>");

               page.w("<body>");
               page.w("<p>All is as good as it getz...</p>");
               
               page.w("<h2>GET Data</h2>");
               page.w("<p>");
                    HeaderList hdrs = request.getHeaders();
                    for (HeaderEntry h : hdrs)
                    {
                         page.w(h.toString(), "<br>");
                    }                    
               page.w("</p>");
               
               page.w("<h2>GET Data</h2>");
               page.w("<p>");
               GETList gets = request.getGETdata();
               for (GETEntry ge : gets)
               {
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("<h2>POST Data</h2>");
               page.w("<p>");
               POSTList posts = ed.getPOSTdata();
               for (POSTEntry ge : posts)
               {
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("<h2>Files</h2>");
               page.w("<p>");
               FileList files = ed.getFileList();
               for (FileEntry ge : files)
               {
                    Path ep = ge.path;
                    if (ep.toFile().exists())
                    {
                         try
                         {
                              final byte[] b = Files.readAllBytes(ep);
                              page.w(new String(b));
                         }
                         catch (IOException ex)
                         {
                              Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    }
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("</body>");
               page.w("</html>");
               
               return page.getHTTPOut();
          }
     }
          
     public static class RH implements seditious.code.http.server.RequestHandler
     {     
          @Override
          public PageHandler yo(HTTPRequest rqst)
          {
               return new ePHbos(rqst);
          }
     }  
     
     public static class ePHNull extends PageHandler
     {
          public ePHNull(HTTPRequest rqst)
          {
               super(rqst);
          }
          
          @Override
          public HTTPOut out(ExtraData ed)
          {              
               final Page page = new Page();
               page.setResponseLine(new HTTPStatus.HTTP200());

               page.w("<!DOCTYPE html>");
               page.w("<html>");
               page.w("<head>");
               page.w("<meta charset=\"UTF-8\">");
               page.w("<title>M'kayz</title>");
               page.w("</head>");

               page.w("<body>");
               page.w("<p>All is as good as it getz...</p>");
               
               page.w("<h2>GET Data</h2>");
               page.w("<p>");
                    HeaderList hdrs = request.getHeaders();
                    for (HeaderEntry h : hdrs)
                    {
                         page.w(h.toString(), "<br>");
                    }                    
               page.w("</p>");
               
               page.w("<h2>GET Data</h2>");
               page.w("<p>");
               GETList gets = request.getGETdata();
               for (GETEntry ge : gets)
               {
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("<h2>POST Data</h2>");
               page.w("<p>");
               POSTList posts = ed.getPOSTdata();
               for (POSTEntry ge : posts)
               {
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("<h2>Files</h2>");
               page.w("<p>");
               FileList files = ed.getFileList();
               for (FileEntry ge : files)
               {
                    Path ep = ge.path;
                    if (ep.toFile().exists())
                    {
                         try
                         {
                              final byte[] b = Files.readAllBytes(ep);
                              page.w(new String(b));
                              final Path cpe = ep.getParent().resolve("kp_" + System.currentTimeMillis());
                              w(cpe);
                              Files.copy(ep, cpe);
                         }
                         catch (IOException ex)
                         {
                              Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    }
                    page.w(ge.toString(), "<br>");
               }
               page.w("</p>");
               
               page.w("</body>");
               page.w("</html>");
               
               return page.getHTTPOut();
          }
     }
          
     public static class RHNull implements seditious.code.http.server.RequestHandler
     {     
          @Override
          public PageHandler yo(HTTPRequest rqst)
          {
               return new ePHNull(rqst);
          }
     }  
     
     public static class Ex implements RequestHandler
     {

          @Override
          public PageHandler yo(HTTPRequest rqst)
          {
               return new StaticPageHandler(rqst);
          }
     }
     
     public static class StaticPageHandler extends PageHandler
     {

          public StaticPageHandler(HTTPRequest rqst)
          {
               super(rqst);
          }

          @Override
          public HTTPOut out(ExtraData exData)
          {
               try
               {
                    HTTPRequest rqst = request;
                    
                    
                    Path p = Paths.get("D:\\Download\\bobos");
                    FileGateKeeper fgk = new FileGateKeeper(p);
                    HTTPOut.HTTPOutBuilder hbs = new HTTPOut.HTTPOutBuilder();
                    
                    URLPath up = request.getPath();
                    if (up.get().length() == 0)
                    {
                         up = new URLPath("/index.html");
                    }
                    
                    final String ct = "Content-Type";
                    
                    if (up.get().contains(".html"))
                    {
                         hbs.setOption(ct, "text/html");
                    }
                    else if (up.get().contains(".png"))
                    {
                         hbs.setOption(ct, "image/png");
                    }
                    else if (up.get().contains(".bmp"))
                    {
                         hbs.setOption(ct, "image/bmp");
                    }
                    else
                    {
                         hbs.setOption(ct, "application/octet-stream");
                    }
                    
                    Path po  = fgk.getPath(up.get());
                    hbs.setOption("Content-Length", po.toFile().length() + "");
                    FileInputStream fis = new FileInputStream(po.toFile()); 
                    hbs.setData(fis);
                    return hbs.build();
               }
               catch (Exception ex)
               {
                    final String msg = "Dub, dah, dubdoo! Imprestarios - internet, hey, some-"
                            + "things went wrong.";
                    return ErrorResponse.getResponse(new HTTPStatus.HTTP404(), null, msg);
               }
          }
     }
}
