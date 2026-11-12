/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Input stream for multi-part content type.
 * @author sedition_act
 */
public class MultiPartInputStream extends InputStream implements AutoCloseable
{     
     //
     public static final String TYPE_RELATED = "related";
     public static final String TYPE_MIXED = "mixed";
     private final long length;
     // private boolean done = false;
     private int msgIndex = 0;
     private final ArrayList<MultiPartMessage> messages;
     private final String boundary = 
             "---------------" + HTTPUtils.getSecRandString(16) +"x" + System.currentTimeMillis();
     
     /**
      * 
      * @param msg The messages.
      */
     public MultiPartInputStream(MultiPartMessage... msg)             
     {
          String mBoundary = "\r\n--" + boundary;
          messages = new ArrayList<>();            
          MultiPartMessage mpm;
          long tl = 0;
          
          for (MultiPartMessage m : msg)
          {               
               mpm = new MultiPartMessage(mBoundary + "\r\n");
               messages.add(mpm);
               messages.add(m);
               tl = tl + mpm.getLength() + m.getLength();
          }
          // insert the lst item - the last boundary
          mpm = new MultiPartMessage(mBoundary + "--\r\n");
          messages.add(mpm);          
          tl = tl + mpm.getLength();
          length = tl;          
     }
     
     @Override
     public int read() throws IOException 
     {    
          int readVal = -1;
          
          while (msgIndex < messages.size())
          {
               MultiPartMessage msg = messages.get(msgIndex); // get the message to read
               readVal = msg.read(); // read 
               
               if (readVal == -1) // end of stream .. move to the enxt message
               {
                    msgIndex++; 
                    if (msgIndex >= messages.size())
                    {
                         readVal = -1;
                         break;
                    }
               }
               else
               {
                    return readVal;
               }
          }
          return readVal;
     }
     
     @Override
     public int read(byte[] b, int off, int len) throws IOException
     {          
          int readLen = -1;
          
          //while (len > 0)
          {          
               while (msgIndex < messages.size())
               {
                    MultiPartMessage msg = messages.get(msgIndex);
                    byte[] temp = new byte[len];
                    readLen = msg.read(temp);
                    
                    if (readLen >= 0)
                    {
                         System.arraycopy(temp, 0, b, off, readLen);    
                         break;
                    }
                    else
                    {
                         msgIndex++; 
                         if (msgIndex >= messages.size())
                         {
                              readLen = -1;
                              break;
                         }
                    }
                    /*
                    if (read >= 0) 
                    // we have some data
                    // read the data .. then reduce the length by the number of bytes we read
                    {
                         bos.write(temp, 0, read);
                         len = len - read;                         
                    }
                    else
                    {
                         msgIndex++;
                    }
                    // if len <= we've run out of buffer space.
                    if (len <= 0)
                    {
                         break;
                    }
                            */
                    
               }
          }       
          /*
          byte[] out = bos.toByteArray();
          len = out.length;
                  
          System.arraycopy(out, 0, b, off, len);
                  */
          return readLen;
     }
     
     @Override
     public int read(byte[] b) throws IOException
     {
          return read(b, 0, b.length);
     }

     /**
      * The length of the message, including boundaries.
      * @return 
      */
     public long length()
     {
          return length;
     }
     
     /**
      * Get the value that will be used in the header.
      * @param subType Type of mutipart message(related, mixed etc.).
      * @return 
      */
     public String getHeaderValue(String subType)
     {
          return "multipart/" + subType + "; boundary=" + boundary;
     }
     
     /**
      * Get the boundary text.
      * @return 
      */
     public String getBoundary()
     {
          return boundary;
     }
     
     @Override
     public void close() throws IOException
     {
          for (MultiPartMessage mpm : messages)
          {
               mpm.close();
          }
     }
}
