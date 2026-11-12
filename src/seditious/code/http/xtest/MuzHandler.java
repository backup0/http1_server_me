/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.HeaderEntry;
import seditious.code.http.common.Headers;
import seditious.code.http.common.URLPath;
import seditious.code.http.server.ErrorResponse;
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
public class MuzHandler implements RequestHandler
{

    /*
        create table object(id identity, deleted boolean, current bigint);
        create table data(id identity, oid bigint, version int, deleted boolean);
     */

     @Override
     public PageHandler yo(HTTPRequest rqst)
     {
          URLPath path = rqst.getPath();
          
          if (path.length() == 0 ||  path.equal("index"))
          {
               return new Index(rqst);
          }
          
          if (path.equal("list"))
          {
              return new ObjList(rqst);
          }

         if (path.equal("post"))
         {
             return new ObjProcessor(rqst);
         }


         if (path.endsWith("vue.js"))
          {
              return new Vue(rqst);              
          }
          
          if (path.length() == 2 && "obj".equals(path.get(0)))
          {
               return new Objs(rqst);
          }
          else
          {
              if (path.equal("file", "vue.js"));
              {
                  System.out.print("vue file ...");
              }
              
              return new PageHandler(rqst) 
              {
                   @Override
                   public HTTPOut out(ExtraData exData)
                   {
                         return ErrorResponse.getResponse(new HTTPStatus.HTTP404(), null,
                                    "Not found ..");
                    }
              };
          }
          
     }
     
     private class Vue extends PageHandler
     {

        public Vue(HTTPRequest rqst)
        {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData)
        {
            byte[] b;
            HTTPOutBuilder ob = new HTTPOutBuilder();
            
            try
            {
                b = Files.readAllBytes(Paths.get("D:\\php\\docs\\vue.js"));
                final String len = b.length + "";
                
                ob.setData(new ByteArrayInputStream(b));
                ob.setOption(new HeaderEntry(Headers.CONTENT_LENGTH, len));
                ob.setOption(new HeaderEntry(Headers.CONTENT_TYPE, "application/javascript; charset=utf-8"));  
                return ob.build();
            } 
            catch (IOException ex)
            {
                b = new byte[]{};
                final String len = b.length + "";
                
                ob.setData(new ByteArrayInputStream(b));
                ob.setOption(new HeaderEntry(Headers.CONTENT_LENGTH, len));
                ob.setOption(new HeaderEntry(Headers.CONTENT_TYPE, "application/javascript; charset=utf-8"));
                return ob.build();
            }
        }
     
     }
     
     private class Index extends PageHandler
     {

        public Index(HTTPRequest rqst)
        {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData)
        {
            byte[] b;
            HTTPOutBuilder ob = new HTTPOutBuilder();
            
            try
            {
                b = Files.readAllBytes(Paths.get("D:\\php\\docs\\index.html"));
                final String len = b.length + "";
                
                ob.setData(new ByteArrayInputStream(b));
                ob.setOption(new HeaderEntry(Headers.CONTENT_LENGTH, len));
                ob.setOption(new HeaderEntry(Headers.CONTENT_TYPE, "text/html; charset=utf-8"));  
                return ob.build();
            } 
            catch (IOException ex)
            {
                b = new byte[]{};
                final String len = b.length + "";
                
                ob.setData(new ByteArrayInputStream(b));
                ob.setOption(new HeaderEntry(Headers.CONTENT_LENGTH, len));
                ob.setOption(new HeaderEntry(Headers.CONTENT_TYPE, "text/html; charset=utf-8"));
                return ob.build();
            }
        }
     
     }
}
