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
import seditious.code.http.common.HTTPMethods;
import static seditious.code.http.xtest.Main.w;

/**
 *
 * @author user
 */
public class ContentBodyTests
{
     /**
      * We have both ...
      * We have none, this should eb treated as .. Multiple rqst. the 2nd one will eb bad .. unless
      *   unless the 2nd request is a proepr http request .. .. don't.
      * We have multiple
      * Length > content .. (if its' single then? If it's mutiple then ..)
      * Length < conntent .. (next will be bad ..)
      * Stop right in the middle - time out then 
      * Close right in the middle ... (user close the connection, do nothing ...)
      */
     
     public void bothTerminators()
     {
          // for chunked 
          byte[] c1 = "a\r\n".getBytes();
          byte[] c2 = "abcdefrtpg\r\n".getBytes();
          byte[] c3 = "b\r\n".getBytes();
          byte[] c4 = "abcdefrtpgf\r\n".getBytes();
          byte[] c5 = "0\r\n\r\n".getBytes();
          
          int fl = c1.length + c2.length + c3.length + c4.length + c5.length;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nBothe terminatorsz");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "chunked" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n").getBytes());
               os.write(("Content-Length: " + fl + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void twinLength()
     {
          // for chunked 
          byte[] c1 = "my body fuckign aches ...".getBytes();
          byte[] c2 = "liek shit is is .. mofos....".getBytes();
          
          int fl = c1.length + c2.length;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nTwin length ..");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Content-Length: " + fl + 21 + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n").getBytes());
               os.write(("Content-Length: " + fl + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);             
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void badLength()
     {
          // for chunked 
          byte[] c1 = "my body fuckign aches ...".getBytes();
          byte[] c2 = "liek shit is is .. mofos....".getBytes();
          
          int fl = c1.length + c2.length;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nBad length ..");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n").getBytes());
               os.write(("Content-Length: " + fl + "a" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);             
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void lengthPlusTranc()
     {
          // for chunked 
          byte[] c1 = "my body fuckign aches ...".getBytes();
          byte[] c2 = "liek shit is is .. mofos....".getBytes();
          
          int fl = c1.length + c2.length;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nlengthPlusTranc ..");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "zip, chunked" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n").getBytes());
               os.write(("Content-Length: " + fl + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);             
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void unSuppportedTransEnc1()
     {
          // for chunked 
          byte[] c1 = "a\r\n".getBytes();
          byte[] c2 = "abcdefrtpg\r\n".getBytes();
          byte[] c3 = "b\r\n".getBytes();
          byte[] c4 = "abcdefrtpgf\r\n".getBytes();
          byte[] c5 = "0\r\n\r\n".getBytes();
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nunSuppportedTransEnc1");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "zip, chunked" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void unSuppportedTransEnc2()
     {
          // for chunked 
          byte[] c1 = "a\r\n".getBytes();
          byte[] c2 = "abcdefrtpg\r\n".getBytes();
          byte[] c3 = "b\r\n".getBytes();
          byte[] c4 = "abcdefrtpgf\r\n".getBytes();
          byte[] c5 = "0\r\n\r\n".getBytes();
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nunSuppportedTransEnc2");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "zip" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void suppportedTransEnc1()
     {
          // for chunked 
          byte[] c1 = "a\r\n".getBytes();
          byte[] c2 = "abcdefrtpg\r\n".getBytes();
          byte[] c3 = "b\r\n".getBytes();
          byte[] c4 = "abcdefrtpgf\r\n".getBytes();
          byte[] c5 = "0\r\n\r\n".getBytes();
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nSuppportedTransEnc1");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "chunked" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void badChunk()
     {
          // for chunked 
          byte[] c1 = "a\r\n".getBytes();
          byte[] c2 = "abcdefrtpg\r\n".getBytes();
          byte[] c3 = "1\r\n".getBytes();
          byte[] c4 = "abcdefrtpgf\r\n".getBytes();
          byte[] c5 = "0\r\n\r\n".getBytes();
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nbadChunk");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Transfer-Encoding: " + "chunked" + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
          
     public void lengthMoreCont()
     {
          // for chunked 
          byte[] c1 = "ahskjgfn".getBytes();
          byte[] c2 = "abcdefrtpgllll".getBytes();
          byte[] c3 = "agrylls".getBytes();
          byte[] c4 = "zzzzz00000213".getBytes();
          byte[] c5 = "0".getBytes();
          
          int len = c1.length + c2.length + c3.length + c4.length + c5.length + 10;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nlengthMoreCont");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Content-Length: " + len + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void lengthLessCont()
     {
          // for chunked 
          byte[] c1 = "ahskjgfn".getBytes();
          byte[] c2 = "abcdefrtpgllll".getBytes();
          byte[] c3 = "agrylls".getBytes();
          byte[] c4 = "zzzzz00000213".getBytes();
          byte[] c5 = "0".getBytes();
          
          int len = c1.length + c2.length + c3.length + c4.length + c5.length - 10;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nlengthMoreCont");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Content-Length: " + len + "\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void noBody()
     {          
          HTTPMethods p0 = HTTPMethods.GET;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nnoBody");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n\r\n").getBytes());
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
     
     public void okBodyLength()
     {
          // for chunked 
          byte[] c1 = "ahskjgfn".getBytes();
          byte[] c2 = "abcdefrtpgllll".getBytes();
          byte[] c3 = "agrylls".getBytes();
          byte[] c4 = "zzzzz00000213".getBytes();
          byte[] c5 = "0".getBytes();
          
          int len = c1.length + c2.length + c3.length + c4.length + c5.length;
          
          HTTPMethods p0 = HTTPMethods.POST;
          try (Socket skt = new Socket((String) null, 80))
          {
               w("\n\nlengthMoreCont");
               OutputStream os = skt.getOutputStream();        
               os.write((p0 + " /upsie_pooopsie http/1.1\r\n").getBytes());
               os.write(("Content-Length: " + len + "\r\n").getBytes());
               os.write(("Crapshoot: nite\r\n").getBytes());
               os.write(("Crapshoot: nite\r\n").getBytes());
               os.write(("Crapshoot: nite\r\n").getBytes());
               os.write(("X-MD5: f323WeFFrtdeFrte\r\n").getBytes());
               os.write(("Content-Type: text/rtf" + "\r\n\r\n").getBytes());
               os.write(c1);  
               os.write(c2);  
               os.write(c3);  
               os.write(c4);  
               os.write(c5);              
               
               ByteArrayOutputStream bos =new ByteArrayOutputStream();
               InputStream ins = skt.getInputStream();
               
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
