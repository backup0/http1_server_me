/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

/**
 * A class for monitoring upload or download progress.
 * @author sedition_act
 */
public abstract class ProgressMonitor
{     
     private final Operation op;
     
     public ProgressMonitor(Operation op)
     {
          this.op = op; 
     }
     
     public Operation operation()
     {
          return op;
     }
     
     /**
      * Method called by the {@code HTTPClient} after some data has been read. The return value tells 
      * the caller whether to continue or to cancel the transfer; {@code true} for cancel, {@code false} to continue.
      * If the operation is cancelled, the client will throw a {@code ClientException}.
      * @param done Number of bytes that has already been read.
      * @param total Number of bytes to read.
      * @return {@code true} tp cancel the transfer, {@code false} otherwise.
      */
     public abstract boolean onProgress(long done, long total);
     
     public enum Operation
     {
          DOWNLOAD, UPLOAD;
     }
             
}
