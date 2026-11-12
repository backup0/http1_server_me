/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static seditious.code.http.xtest.Main.w;

/**
 *
 * @author user
 */
public class HeaderTests
{
     public void sendSomeThenStop()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("sendSomeThenStop()");                
               os.write("GET / HTTP/1.1\r\n".getBytes()); 
               os.write("Authorization: xyz\r\n".getBytes()); 
               os.write("Cookie: Poops\r".getBytes()); 
               os.flush();
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }  
     }
     
     public void waitTillTimeout()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("waitTillTimeout()");
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }  
     
     }
     
     public void writeThenClose()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("writeThenClose()");                
               os.write("GET / HTTP/1.1\r\n".getBytes()); 
               os.write("Authorization: xyz\r\n".getBytes()); 
               os.write("Cookie: Poops\r".getBytes()); 
               os.flush();
               
               skt.shutdownOutput();
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) 
          {
              Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
          }  
     }
     
     public void ok()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("okz()");                
               os.write("GET / HTTP/1.1\r\n".getBytes()); 
               os.write("Authorization: xyz\r\n".getBytes()); 
               os.write("Cookie: Poops\r\n\r\n".getBytes()); 
               os.flush();
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }  
     }
     
     public void badMethod()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("badMethod()");                
               os.write("DET / HTTP/1.1\r\n".getBytes()); 
               os.write("Authorization: xyz\r\n".getBytes()); 
               os.write("Cookie: Poops\r\n\r\n".getBytes()); 
               os.flush();
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }  
     }
     
     public void badUrl()
     {
          try (Socket skt = new Socket((String) null, 80);
                  OutputStream os = skt.getOutputStream();
                  InputStream ins = skt.getInputStream())
          {
               w("\n\n");
               w("badUrl()");                
               os.write("GET / xyz HTTP/1.1\r\n".getBytes()); 
               os.write("Authorization: xyz\r\n".getBytes()); 
               os.write("Cookie: Poops\r\n\r\n".getBytes()); 
               os.flush();
               
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               
               int i;
               byte[] b = new byte[1024];
               
               while ((i = ins.read(b)) >= 0)
               {
                    bos.write(b, 0, i);
               }
               w(bos.toString());
          }
          catch (IOException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }  
     }
}
