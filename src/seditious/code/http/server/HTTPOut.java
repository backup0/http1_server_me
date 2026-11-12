package seditious.code.http.server;

import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.StringPair;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class HTTPOut {

     private HTTPStatus status = new HTTPStatus.HTTP500();
     private final ArrayList<StringPair> headers = new ArrayList<>();
     private InputStream usrData = new ByteArrayInputStream(new byte[0]);

     public void setStatus(HTTPStatus status) {
          this.status = status;
     }

     /**
      * Set header.
      * @param name Header name
      * @param value Value.
      */
     public void setOption(String name, String value) {
          headers.add(new StringPair(name, value));
     }

     /**
      * Output data. This is the response (file, text, etc.).
      * @param ins The input stream. This is the date to write out.
      */
     public void setData(InputStream ins) {
          usrData = ins;
     }

     /**
      * Get header.
      * @return List containing headers.
      */
     public List<StringPair> getHeaders() {
          return headers;
     }
     
     /**
      * Get InputStream
      * @return InputStream set, or if not set empty {@code ByteArrayInputStream}.
      */
     public InputStream getData() {
         return usrData;
     }

     /**
      * Get status.
      * @return HTTP status.
      */
     public HTTPStatus getStatus() {
          return status;
     }
}
