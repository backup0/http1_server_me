/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.xtest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 *
 * @author user
 */
public class TestInputStream extends InputStream
{
     private final ByteArrayInputStream _bis;
     private boolean _throwTimeOut = false;
     private boolean _throwIOEx = false;
     
     public TestInputStream(byte[] data)
     {
          _bis = new ByteArrayInputStream(data);
     }
     
     @Override
     public int read() throws IOException
     {
          if (_throwIOEx)
          {
               throw new IOException();
          }
          
          int r = _bis.read();
          if (r == -1 && _throwTimeOut)
          {
               throw new SocketTimeoutException();
          }
          else
          {
               return r;
          }
     }
     
     @Override
     public int read(byte[] b) throws IOException
     {
          if (_throwIOEx)
          {
               throw new IOException();
          }          
          
          int r = _bis.read(b);
          if (r == -1 && _throwTimeOut)
          {
               throw new SocketTimeoutException();
          }
          else
          {
               return r;
          }
     }
     
     @Override
     public int read(byte[] b, int a, int c) throws IOException
     {
          if (_throwIOEx)
          {
               throw new IOException();
          }
          
          int r = _bis.read(b, a, c);
          if (r == -1 && _throwTimeOut)
          {
               throw new SocketTimeoutException();
          }
          else
          {
               return r;
          }
     }
     
     public void throwTimeout()
     {
          _throwTimeOut = true;
     }
     
     public void unThrowTimeout()
     {
          _throwTimeOut = false;
     }
     
     public void throwIOEx()
     {
          _throwIOEx = true;
     }
     
     public void unThrowIO()
     {
          _throwIOEx = false;
     }
     
     @Override
     public void reset()
     {
          _bis.reset();
     }
     
     /**
      * try (TestInputStream ins = new TestInputStream(new byte[]{12, 12, 12});
                  BufferedInputStream bis = new BufferedInputStream(ins))
          {
               //ins.throwTimeout();
               ins.throwIO();
               w((new ContentBodyHandler()).consume(hdrList, bis));
          }
      */
}
