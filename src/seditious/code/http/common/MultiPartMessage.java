/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * Message for {@code MultiPartInputStream}.
 * @author sedition_act
 */
public class MultiPartMessage implements AutoCloseable
{         
     /**
      * Create a message with a file.
      * @param f
      * @param hdr Header.
      * @throws FileNotFoundException 
      */
     public MultiPartMessage(File f, HashMap<String, String> hdr) throws FileNotFoundException
     {          
          file = f; 
          header = getHeader(hdr);
          totalLength = header.length + f.length();         
          in = new BufferedInputStream(new FileInputStream(file));
     }
     
     /**
      * Create a message with bytes.
      * @param b
      * @param hdr Header.
      */
     public MultiPartMessage(byte[] b, HashMap<String, String> hdr)
     {
          file = null;
          header = getHeader(hdr);
          totalLength = header.length + b.length;
          in = new BufferedInputStream(new ByteArrayInputStream(b));
     }     
     
     /**
      * Constructor for creating boundary message. This is used by {@code MultiPartInputStream}.
      * @param boundary 
      */
     public MultiPartMessage(String boundary)
     {
          file = null;
          header = new byte[0];
          byte[] b = boundary.getBytes(StandardCharsets.US_ASCII);
          totalLength = b.length;
          in = new BufferedInputStream(new ByteArrayInputStream(b));
     }    
          
     /**
      * Read a byte.
      * @return
      * @throws IOException 
      */
     public int read() throws IOException
     {
          int b;          
          
          if (readIndex[0] < header.length) // still reading the header
          {           
               b = convertByteToInt(header[readIndex[0]]);
               readIndex[0]++;          
          }
          else // done read the other one.
          {
               b = in.read();
          }                               
          return b;
     }
     
     private int convertByteToInt(byte b)
     {
          if (b < 0)
          {
               return (int) (b + 128) + 128;
          }
          else
          {    
               return (int) b;
          }
     }
     
     /**
      * Read data into {@code b}
      * @param b
      * @return
      * @throws IOException 
      */
     public int read(byte[] b) throws IOException
     {          
          int tRead = 0;  
          
          if (readIndex[0] < header.length)
          {                    
               while (readIndex[0] < header.length && tRead < b.length)
               {
                    b[tRead] = header[readIndex[0]];
                    readIndex[0]++; tRead++;
               }              
          }
          else
          {      
               tRead = in.read(b, tRead, b.length - tRead); // if we have read the whole thing
                    // this will return -1;
          }      
          return tRead;
     }
          
     private byte[] getHeader(HashMap<String, String> hdr)
     {
          if (hdr == null)
          {
               hdr = new HashMap<>();
          }
          
          // 
          String val;
          StringBuilder sb = new StringBuilder("");
          Set<String> ks = hdr.keySet();
          
          for (String key : ks)
          {
               val = hdr.get(key);
               if (val != null)
               {
                    sb.append(key).append(": ").append(val).append("\r\n");
               }
          }          
          
          sb.append("\r\n");
          
          return sb.toString().getBytes(StandardCharsets.US_ASCII);
     }
     
     /**
      * Get the length of the message, this includes the length of the boundary.
      * @return 
      */
     public long getLength()
     {
          return totalLength;
     }
     
     @Override
     public void close() throws IOException
     {
          in.close();
     }

     private final byte[] header;
     private final BufferedInputStream in;
     private final File file;
     private final int[] readIndex = new int[]{0, 0};
     private final long totalLength;
}
