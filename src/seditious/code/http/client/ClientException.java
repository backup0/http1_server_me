/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.client;

/**
 * Exception thrown by the client.
 * @author sedition_act
 */
public class ClientException extends Exception {

     /**
      * Reason.
      */
     public final Reason reason;

     public ClientException() {
          this(Reason.UNKNOWN);
     }

     public ClientException(Reason r) {
          super("HTTP client exception - " + r.name() + ".");
          reason = r;
     }

     public enum Reason
     {
          CONNECTION_CLOSED,
          BAD_URL,
          BAD_HEADER,
          LENGTH_MISMATCH,
          TRANSFER_ENCODING_NOT_SUPPORTED,
          BAD_ARGUMENT,
          OP_CANCELLED_BY_USER,
          CLIENT_CLOSED,
          CHUNK_READER_ERROR,
          STATE_EXCEPTION,
          IO_EXCEPTION,
          SSL_EXCEPTION,
          TIMEOUT,
          RECEIVED_EARLY_RESPONSE,
          UNKNOWN
     }
}
