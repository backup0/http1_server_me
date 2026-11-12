/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.Main;
import seditious.code.http.common.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class OutputWriter
{
     private final BufferedOutputStream _outs;
     private final HTTPOut _httpOut;
     private final List<StringPair> _additionalHeaders;
     private final ByteArrayOutputStream _hdrInBytes = new ByteArrayOutputStream();
     private final byte[] _buffer;
     private final InputStream _data;
     private final boolean _closeCon;

     private int dataLength = 0;

     private boolean _failed = false;

     public OutputWriter(HTTPOut resp, OutputStream outs, boolean closeConn, int bufferSize) {

          _outs = new BufferedOutputStream(outs);
          _httpOut = resp;

          final var x = correctUserError(resp);
          if (x == null)
          {
               _data = resp.getData();
               _closeCon = closeConn;
          }
          else
          {
               _httpOut.setStatus(new HTTPStatus.HTTP500());
               _data = x;
               _closeCon = true;
          }
          _additionalHeaders = getAdditionalHeaders(resp);
          getHeaderBytes();
          _buffer = new byte[bufferSize];
     }

     private ByteArrayInputStream correctUserError(HTTPOut resp)
     {
          boolean no = true;
          for (StringPair sp : _httpOut.getHeaders())
          {
               if (sp.getKey().equalsIgnoreCase(Headers.CONTENT_LENGTH))
               {
                    try
                    {
                         dataLength = Integer.parseInt(sp.getValue());
                    }
                    catch (Exception e)
                    {
                         break;
                    }
                    no = false;
                    break;
               }
          }

          if (!no)
          {
               return null;
          }

          final byte[] ys = "some kind of user error.".getBytes(StandardCharsets.UTF_8);
          ByteArrayInputStream bis = new ByteArrayInputStream(ys);

          resp.getHeaders().clear();
          resp.getHeaders().add(new StringPair(Headers.CONTENT_LENGTH, "" + ys.length));
          resp.getHeaders().add(new StringPair(Headers.CONTENT_TYPE, "text/plain"));

          return bis;
     }
     
     private List<StringPair> getAdditionalHeaders(HTTPOut resp) {

          final ArrayList<StringPair> hdrs = new ArrayList<>();
          hdrs.add(new StringPair("Date", HTTPUtils.getDate()));

          // todo: do we even need this?
          final String[] connHdr = ListHelper.getValues(Headers.CONNECTION, resp.getHeaders());
          if (connHdr.length == 0) {
               hdrs.add(new StringPair("Connection", (_closeCon) ? "close" : "keep-alive"));
          }

          return hdrs;
     }
     
     private void getHeaderBytes() {

          final String httpVersion = "HTTP/1.1";
          final HTTPStatus status = _httpOut.getStatus();

          try {
               final String rLine = httpVersion + " " + status.getCode() + " " + status.getMessage() + "\r\n";
               _hdrInBytes.write(rLine.getBytes(StandardCharsets.US_ASCII));

               for (StringPair he : _httpOut.getHeaders()) {
                    final byte[] b = (he.getKey() + ": " + he.getValue() + "\r\n").getBytes(StandardCharsets.US_ASCII);
                    _hdrInBytes.write(b);

               }

               for (StringPair he : _additionalHeaders) {
                    final byte[] b = (he.getKey() + ": " + he.getValue() + "\r\n").getBytes(StandardCharsets.US_ASCII);
                    _hdrInBytes.write(b);
               }
               _hdrInBytes.write("\r\n".getBytes(StandardCharsets.US_ASCII));
          }
          catch (Exception e) {
               _failed = true;
          }
     }

     /**
      * 0 - DONE
      * 1 - OK
      * 2 - ERROR
      * @return
      */
     public int send() {

          if (_failed) {
               return 2;
          }

          try {
               //final int hdrLen;
               if (_hdrInBytes.size() > 0) {
                    _outs.write(_hdrInBytes.toByteArray());
                    _outs.flush();
                    _hdrInBytes.reset();
                    return 1;
               }

               final int len = _data.read(_buffer);
               if (len > 0) {
                    _outs.write(_buffer, 0, len);
                    _outs.flush();
                    dataLength=-len;
                    return 1;
               }
               else if (len == 0)
               {
                    return 1;
               }
               else
               {
                    // we're done.
                    _data.close();
                    if (dataLength == 0)
                    {
                         return 0;
                    }
                    else
                    {
                         _failed = true;
                         return 2;
                    }
               }
          }
          catch (Exception e) {
               _failed = true;
               return 2;
          }
     }
}
