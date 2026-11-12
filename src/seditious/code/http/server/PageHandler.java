/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import java.io.OutputStream;

/**
 * This handles a request. From start to finish.
 * @author user
 */
public abstract class PageHandler
{
     /**
      * Request instance.
      */
     public final HTTPRequest request;
     
     /**
      * 
      * @param rqst HTTPRequest instance.
      */
     public PageHandler(HTTPRequest rqst) {
          request = rqst;
     }

     /**
      * Override this method to pass an output stream to write the content body to, otherwise, the
      * server will write it to a temporary file. The output stream will be used as is, so it
      * is highly recommended that the output stream is wrapped inside a buffered stream.
      * @return OuputStream to write data to.
      */
     public OutputStream in() {
          return null;
     }
     
     /**
      * Called by the server to make sure that output stream passed by the user is closed. If you need
      * to use the output stream prior to closing, override this method (then make sure 
      * <code>OutputStream</code> is closed).
      * @param os 
      * @param success {@code true} if data is written successfully to output stream.
      */
     public void doneIn(OutputStream os, boolean success) {
          try {
               if (os != null) {
                    os.close();
               }
          }
          catch (Exception ignored) {}
     }

     /**
      * Pass output. This is the data to send back to the client. The only method that has to be
      * overridden.
      * @param exData Contains list of files uploaded, and POST values (sent with 
      *   <i>application/x-www-form-urlencoded</i> content type).
      * @return 
      */
     public abstract HTTPOut out(ExtraData exData);

     @Override
     public String toString()
     {
          return request.toString();
     }
}
