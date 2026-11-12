/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.client;

import seditious.code.http.common.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// todo: check timeout exception while reading content ...

/**
 *
 * @author sedition_act
 */
public class HTTPClient implements AutoCloseable
{
     private enum Ops {
          RESET_FOR_REQUEST,
          CREATE_REQUEST,
          SET_OPTIONS,
          SEND_REQUEST,
          READ_RESPONSE,
          CLOSE_SOCKET
     }

     // stuff we can change related to request/response
     private Ops _lastOp = Ops.RESET_FOR_REQUEST;
     private HTTPMethods _method = null;
     private byte[] _requestLine = null;
     private final ArrayList<StringPair> _requestHeaders = new ArrayList<>();
     private ResponseHeader _responseHeader = null;

     // some other stuff
     private final ByteArrayOutputStream _remainder = new ByteArrayOutputStream();

     // read time out.
     private int _timeout = 0;

     private final ConnSetting _connSettings;
     private final int BUFFER_SIZE = (1024 * 4); //todo: this could be any number, the bigger - right, I want to monitor

     private final Socket _socket;
     private final BufferedInputStream _inStream;
     private final BufferedOutputStream _outStream;

     private final ByteArrayOutputStream _earlyResponse = new ByteArrayOutputStream();

     /**
      * Create a connection. If host is <code>null</code>, connection to localhost (loopback) will be created.
      * @param host The host name (eg. www.example.com)
      * @param port Port.
      * @param secure Secure or insecure connection(https or http), this determines the socket to use.
      * @throws IOException 
      */
     public HTTPClient(String host, int port, boolean secure) throws IOException
     {          
          _connSettings = new ConnSetting(host, null, port, secure);
          _socket = createConnection();
          _inStream = new BufferedInputStream(_socket.getInputStream());
          _outStream = new BufferedOutputStream(_socket.getOutputStream());
     }     
     
     /**
      * Create a connection.
      * @param a Inet address of the remote host.
      * @param port Port.
      * @param secure Secure or insecure connection(https or http), this determines the socket to use.
      * @throws IOException 
      */
     public HTTPClient(InetAddress a, int port, boolean secure) throws IOException
     {
          // Numeric ip.
          _connSettings = new ConnSetting(a.getHostAddress(), a, port, secure);
          _socket = createConnection();
          _inStream = new BufferedInputStream(_socket.getInputStream());
          _outStream = new BufferedOutputStream(_socket.getOutputStream());
     }
     
     /**
      * Create a connection.
      * @param up
      * @throws IOException
      */
     public HTTPClient(URLParser up) throws IOException {
          _connSettings = new ConnSetting(up.host, null, up.port, up.secure);
          _socket = createConnection();
          _inStream = new BufferedInputStream(_socket.getInputStream());
          _outStream = new BufferedOutputStream(_socket.getOutputStream());
     }
     
     /**
      * Create a connection.
      * @param url The url.
      * @throws IOException
      * @throws ClientException 
      */
     public HTTPClient(String url) throws IOException, ClientException
     {  
          final URLParser up = new URLParser(url);
          _connSettings = new ConnSetting(up.host, null, up.port, up.secure);
          _socket = createConnection();
          _inStream = new BufferedInputStream(_socket.getInputStream());
          _outStream = new BufferedOutputStream(_socket.getOutputStream());
     }
     
     private Socket createConnection() throws IOException
     {
          final Socket socket;
          if (_connSettings.inet == null) {
               if (_connSettings.secure) {
                    // Class SSLServerSocketFactory
                    socket = SSLSocketFactory.getDefault().createSocket(_connSettings.host, _connSettings.port);
                    ((SSLSocket) socket).startHandshake();
               }
               else {
                    socket = new Socket(_connSettings.host, _connSettings.port);
               }  
          }
          else {
               if (_connSettings.secure) {
                    socket = SSLSocketFactory.getDefault().createSocket(_connSettings.inet, _connSettings.port);
               }
               else {
                    socket = new Socket(_connSettings.inet, _connSettings.port);
               } 
          }
          return socket;
     }
     
     // Reset stuff we need to reset to prepare for a new request.
     private void resetForRequest()
     {
          _lastOp = Ops.RESET_FOR_REQUEST;
          _method = null;
          resetResponseFields();
     }

     private void resetResponseFields() {
          _requestLine = null;
          _responseHeader = null;
          _requestHeaders.clear();
          _remainder.reset();
     }
     
     /**
      * Create a request.
      * @param method HTTP method to use.
      * @param path Path. 
      * @throws seditious.code.http.client.ClientException 
      */
     public void createRequest(HTTPMethods method, String path) throws ClientException {
          createRequest(method, path, null, false);
     }
     
     /**
      * Create a request with query string.
      * @param method HTTP method to use.
      * @param path Path. 
      * @param args Query parameters to send with the request. 
      * @throws seditious.code.http.client.ClientException 
      */
     public void createRequest(HTTPMethods method, String path, Map<String, String> args)
             throws ClientException {
          createRequest(method, path, args, false);
     }

     /**
      * Create a request with query string, also specifies how spaces should be encoded.
      * @param method HTTP method to use.
      * @param path Path.
      * @param args Query parameters to send with the request.
      * @param usePlus Whether to use plus sign (+) instead of %20 for encoding space.
      * @throws seditious.code.http.client.ClientException
      */
     public void createRequest(HTTPMethods method, String path, Map<String, String> args, boolean usePlus)
             throws ClientException {

          if (method == null || path == null) {
               close();
               throw new ClientException(ClientException.Reason.BAD_ARGUMENT);
          }

          checkIfOpIsAllowed(Ops.CREATE_REQUEST);
          _lastOp = Ops.CREATE_REQUEST;

          if (args != null) {
               path = path + "?" + HTTPUtils.buildQueryString(args, usePlus);
          }
          
          final StringBuilder sb = new StringBuilder();

          _method = method;
          sb.append(method.name());
          sb.append(" ");
          sb.append(path);
          sb.append(" ").append("HTTP/1.1").append("\r\n");
          _requestLine = sb.toString().getBytes(StandardCharsets.US_ASCII);
          // header stuff
          _requestHeaders.add(new StringPair(Headers.HOST, _connSettings.host));
          _requestHeaders.add(new StringPair(Headers.CONNECTION, "keep-alive"));
     }     
     
     /**
      * Set header field.
      * @param option Header field.
      * @throws seditious.code.http.client.ClientException
      */
     public void setRequestHeader(StringPair option) throws ClientException {
          // should only be allowed after create request,
          // don't record it - isn't necessary
          checkIfOpIsAllowed(Ops.SET_OPTIONS);
          _requestHeaders.add(option);
     }

     /**
      *
      * @param k
      * @param v
      * @throws ClientException
      */
     public void setRequestHeader(String k, String v) throws ClientException {
          // should only be allowed after create request,
          // don't record it - isn't necessary
          checkIfOpIsAllowed(Ops.SET_OPTIONS);
          _requestHeaders.add(new StringPair(k, v));
     }

     /**
      * Send request; for a request that does not expect content body.
      * @throws ClientException
      */
     public void sendRequest() throws ClientException {
          sendRequest(new ByteArrayInputStream(new byte[]{}), 0, null);
     }

     /**
      * Send request along with content body.
      * @param in
      * @param length
      * @throws ClientException
      */
     public void sendRequest(InputStream in, long length) throws ClientException {
          sendRequest(in, length, null);
     }

     /**
      * Send request along with content body. This blocks until the request has been sent.
      * @param in
      * @param length
      * @param pm
      * @throws ClientException
      */
     public void sendRequest(InputStream in, long length, ProgressMonitor pm) throws ClientException {

          checkIfOpIsAllowed(Ops.SEND_REQUEST);
          _lastOp = Ops.SEND_REQUEST;

          if (length < 0) {
               throw new ClientException(ClientException.Reason.BAD_ARGUMENT);
          }

          // if in put stream is null - null pointer exception then, why should we check?
          try (BufferedInputStream bis = new BufferedInputStream(in)) {
               // send header
               sendPartOneOfRequest(length);
               // send content body - if there's any.
               sendPartTwoOfRequest(bis, length, pm);

               do {
                    // because 100 can still arrive after
                    readHeader(false);
               } while(_responseHeader.code.equalsIgnoreCase("100"));
               // by this time header would have be read
               // user can access the header.
          }
          catch (ClientException ex) {
               close();
               throw ex;
          }
          catch (Exception ex) {
               close();
               ex.printStackTrace();
               throw new ClientException(ClientException.Reason.UNKNOWN);
          }
     }

     // send header part
     private void sendPartOneOfRequest(long length) throws IOException {

          // accept anything unless user specifies
          if (!ListHelper.hasKey(Headers.ACCEPT, _requestHeaders)) {
               _requestHeaders.add(new StringPair(Headers.ACCEPT, "*/*"));
          }

          // set content length field; which is required for some.
          final String sLength = String.valueOf(length);
          switch (_method) {
               case CONNECT:
               case PATCH:
               case POST:
               case PUT:
               case OPTIONS:
                    _requestHeaders.add(new StringPair(Headers.CONTENT_LENGTH, sLength));
                    break;
               case GET:
                    if (length > 0) {
                         _requestHeaders.add(new StringPair(Headers.CONTENT_LENGTH, sLength));
                    }
                    break;
               default:
                    break;
          }

          // request is in byte.
          _outStream.write(_requestLine);
          // Get header ..
          _outStream.write(getHeaderInBytes());
          _outStream.flush();
     }

     private void sendPartTwoOfRequest(InputStream cbIns, long cbLen, ProgressMonitor pm)
             throws IOException, ClientException {

          final byte[] buffer = new byte[BUFFER_SIZE];
          long readSoFar = 0;
          int cbInsRead;
          while ((cbInsRead = cbIns.read(buffer)) != -1) {

               // receive response early that isn't 100?
               // exception will be thrown - no need for return value.
               readInterimResponse();
               _outStream.write(buffer, 0, cbInsRead);
               //no error if we don;t read interim. wtf is this bs?

               // monitoring crap, don't worry about it.
               readSoFar = readSoFar + cbInsRead;

               if (reportProgress(pm, readSoFar, cbLen)) {
                    throw new ClientException(ClientException.Reason.OP_CANCELLED_BY_USER);
               }
          }

          // the number of bytes we sent does not match what the user ??
          if (readSoFar != cbLen) {
               throw new ClientException(ClientException.Reason.LENGTH_MISMATCH);
          }
          _outStream.flush();
     }

     private void readInterimResponse() throws IOException, ClientException {

          // if there's something - the entire header will be read first.
          // timing is handled by - read header.
          readHeader(true);
          if (_responseHeader == null) {
               return;
          }
          // 100 -> ok, else?
          // 100 is continue; so it's ok ...
          // else, assume error, why? Why send other stuff?
          // 100 does not have content body ...
          if ("100".equalsIgnoreCase(_responseHeader.code)) {
               // 100 is ok.
               resetResponseFields();
          }
          else {
               doReadContent(_earlyResponse,null); //??? only if we have something
               throw new ClientException(ClientException.Reason.RECEIVED_EARLY_RESPONSE);
          }
     }

     public String[] readResponseLine() throws ClientException {

          checkIfOpIsAllowed(Ops.READ_RESPONSE);

          if (_responseHeader == null) {
               return new String[0];
          }

          return new String[]{_responseHeader.httpVersion, _responseHeader.code, _responseHeader.message};
     }

     /**
      * get a header entry for the {@code key}
      * @param key Array containing {@code HeaderEntry} for the {@code key}, 
      * or an empty array if the {@code key} is not part of the header.
      * @return 
      * @throws seditious.code.http.client.ClientException 
      */
     public String[] getResponseHeader(String key) throws ClientException
     {
          // this has to be after send request. .. so, id read content is valid then this is valid
          checkIfOpIsAllowed(Ops.READ_RESPONSE);
          return ListHelper.getValues(key, _responseHeader.headers);
     }
     
     /**
      * Get all header entries for this response.
      * @return 
      * @throws seditious.code.http.client.ClientException 
      */
     public List<StringPair> getResponseHeaders() throws ClientException
     {
          // this has to be after send request. .. so, id read content is valid then this is valid
          checkIfOpIsAllowed(Ops.READ_RESPONSE);
          return _responseHeader.headers;
     }
     
     private byte[] getHeaderInBytes()
     {
          final StringBuilder sb = new StringBuilder();
          
          for (StringPair he : _requestHeaders) {
               sb.append(he.getKey());
               sb.append(":");
               sb.append(he.getValue());
               sb.append("\r\n");
          }
          sb.append("\r\n");
          return sb.toString().getBytes(StandardCharsets.US_ASCII);
     }
     
     private void readHeader(boolean returnImmediately) throws IOException, ClientException
     {
          final HeaderReader headerReader = new HeaderReader();
          HeaderReader.Status status = HeaderReader.Status.NOT_FOUND_YET;

          _socket.setSoTimeout(1);
          try {

               final int i = _inStream.read();
               if (i == -1) {
                    throw new ClientException(ClientException.Reason.CONNECTION_CLOSED);
               }
               status = headerReader.update(i);
          }
          catch (SocketTimeoutException ignored) {
               if (returnImmediately) {
                    return;
               }
          }

          _socket.setSoTimeout(_timeout);

          int read;
          // Read header first
          while ((read = _inStream.read()) >= 0) {
               status = headerReader.update(read);
               if (status != HeaderReader.Status.NOT_FOUND_YET) {
                    break;
               }
          }

          switch (status) {
               case NOT_FOUND_YET:
               case MALFORMED:
                    throw new ClientException(ClientException.Reason.BAD_HEADER);
               case DONE:
                    _responseHeader = (new ResponseHeader(headerReader));
                    break;
               default:
                    throw new ClientException(ClientException.Reason.UNKNOWN);
          }
     }
     
     /**
      * Read content of response, the response is simply "consumed". 
      * @throws ClientException
      */
     public void readContent() throws ClientException {
          readContent(null, null);
     }
     
      /**
      * Read the content of this request into the output stream.
      * @param os Output stream where the data will be written to.
      * @throws seditious.code.http.client.ClientException
      */
     public void readContent(OutputStream os) throws ClientException {
          readContent(os, null);
     }

     public void readContent(OutputStream os, ProgressMonitor pm) throws ClientException {

          if (_lastOp == Ops.RESET_FOR_REQUEST) {
               throw new ClientException(ClientException.Reason.STATE_EXCEPTION);
          }
          // that's because I insist on using the same flag for reading the header and this; yes.
          checkIfOpIsAllowed(Ops.READ_RESPONSE);

          try {
               doReadContent(os, pm);
          }
          catch (ClientException ex) {
               close();
               throw ex;
          }
          catch (IOException ioe) {
               close();
               throw new ClientException(ClientException.Reason.IO_EXCEPTION);
          }
          catch (Exception ex) {
               close();
               throw new ClientException(ClientException.Reason.UNKNOWN);
          }
     }

     /**
      * Read the content of this request into the output stream.
      * @param os Output stream where the data will be written to.
      * @param pm Callback for monitoring progress. Note that if the content is transferred in chunks
      *   the total value pass to the callback is always 1, and the done value is always 0, 
      *   since it is impossible to determine the length of the content.
      * @throws seditious.code.http.client.ClientException
      */
     private void doReadContent(OutputStream os, ProgressMonitor pm) throws ClientException, IOException {

          if (os == null) {
               os = new DummyOutputStream();
          }

          try (BufferedOutputStream bos = new BufferedOutputStream(os)) {

               final String[] transferEncoding;
               transferEncoding = ListHelper.getValues(Headers.TRANSFER_ENCODING, _responseHeader.headers);

               // do the reading.
               if (transferEncoding.length > 0) {
                    readChunkedData(bos, pm);
               }
               else {
                    readByLength(bos, pm);
               }

               final String[] conn = ListHelper.getValues(Headers.CONNECTION, _responseHeader.headers);
               final boolean close = conn.length > 0 && conn[0].equalsIgnoreCase("close");

               if (close) {
                    close();
               }
               else {
                    resetForRequest();
               }
          }
     }

     private void readChunkedData(OutputStream os, ProgressMonitor pm) throws IOException, ClientException {

          final byte[] buffer = new byte[BUFFER_SIZE];
          final String[] encType = ListHelper.getValues(Headers.TRANSFER_ENCODING, _responseHeader.headers);

          if (encType.length > 0 && encType[0].contains("chunked")) {

               // read the chunk, how do we do that again?
               final ChunkReader cr = new ChunkReader(os);

               boolean cont = true;
               int read;
               while (cont) {

                    final byte[] b;
                    if (_remainder.size() > 0) {
                         b = _remainder.toByteArray();
                         _remainder.reset();
                    }
                    else {
                         read = _inStream.read(buffer);
                         if (read == -1) {
                              throw new ClientException(ClientException.Reason.CONNECTION_CLOSED);
                         }
                         b = Arrays.copyOf(buffer, read);
                    }

                    cr.addData(b);
                    cr.update();

                    switch (cr.getStat()) {
                         case NOT_DONE:
                              cont = true;
                              break;
                         case OK:
                              cont = false;
                              _remainder.write(cr.getRemainder());
                              break;
                         case BAD_REQUEST:
                         case SERVER_ERROR:
                         default:
                              throw new ClientException(ClientException.Reason.CHUNK_READER_ERROR);
                    }

                    // 0, 1 because we don't know the length
                    if (reportProgress(pm, 0, 1)) {
                         throw new ClientException(ClientException.Reason.OP_CANCELLED_BY_USER);
                    }
               }
          }
          else {
               throw new ClientException(ClientException.Reason.TRANSFER_ENCODING_NOT_SUPPORTED);
          }
     }

     private void readByLength(OutputStream os, ProgressMonitor pm) throws ClientException, IOException {

          long readSoFar = 0;
          final String[] contLen = ListHelper.getValues(Headers.CONTENT_LENGTH, _responseHeader.headers);

          final long total;
          if (contLen.length <= 0) {
               total = 0;
          }
          else {
               total = Integer.parseInt(contLen[0]);
          }

          long remaining = total;

          final byte[] buffer = new byte[BUFFER_SIZE];
          int read;
          while (remaining > 0) {
               read = _inStream.read(buffer);

               if (read == -1) {
                    throw new ClientException(ClientException.Reason.CONNECTION_CLOSED);
               }
               // do this
               // Need to read it here then..
               os.write(buffer, 0, read);
               readSoFar = readSoFar + read;
               remaining = remaining - read;

               if (reportProgress(pm, readSoFar, total)) {
                    throw new ClientException(ClientException.Reason.OP_CANCELLED_BY_USER);
               }
          }
     }
     
     private boolean reportProgress(ProgressMonitor pm, long done, long total) {
          boolean stop = false;
          if (pm != null) {
               stop = pm.onProgress(done, total);
          }
          return stop;
     }
     
     /**
      * Get the socket for this connection. 
      * @return Socket, or null if there isn't one.
      */
     public Socket getSocket() {
          return _socket;
     }
     
     /**
      * Set timeout in seconds.
      * @param to Timeout period, in seconds. <= 0 means infinite.
      */
     public void setTimeout(int to) {
          if (to < 0) {
               to = 0;
          }
          _timeout = to * 1000;
     }
     
     /**
      * Close the connection. 
      */
     @Override
     public void close() {
          _lastOp = Ops.CLOSE_SOCKET;

          try {
               _socket.close();
          }
          catch (Exception ignored) {// well, why bother?
          }
     }
          
     /**
      * Check if the socket is closed.
      * @return <code>true</code> if the socket is closed, <code>false</code> if otherwise.
      */
     public boolean isClosed() {
          return _socket.isClosed();
     }

     private void checkIfOpIsAllowed(Ops op) throws ClientException {

          final boolean valid;
          switch (op) {               
               case CREATE_REQUEST:
                    valid = _lastOp == Ops.RESET_FOR_REQUEST;
                    break;
               case SET_OPTIONS:
               case SEND_REQUEST:
                    valid = _lastOp == Ops.CREATE_REQUEST;
                    break;
               case READ_RESPONSE:
                    // 1: read header
                    // 2: read content
                    // 3: read header after this has been closed. Useful if we receive response early.
                    valid = (_lastOp == Ops.SEND_REQUEST) || (_lastOp == Ops.CLOSE_SOCKET);
                    // it's ok to read after we've sent request, ..
                    // and also after it has been closed.
                    break;
               default:
                    // todo: correct?
                    valid = false;
                    break;
          }

          if (!valid) {
               throw new ClientException(ClientException.Reason.STATE_EXCEPTION);
          }
     }
     
     private static class ConnSetting
     {
          public String host;
          public InetAddress inet;
          public int port;
          public boolean secure;

          public ConnSetting(String hst, InetAddress ina, int prt, boolean sec) {
               host = hst;
               inet = ina;
               port = prt;
               secure = sec;
          }
     }     
     
     /**
      * Xyz ..
      */
     private static class ResponseHeader
     {
          public final List<StringPair> headers;
          public final String code;
          public final String message;
          public final String httpVersion;

           /**
           * 
           * @param hdr Header.
           */
          public ResponseHeader(HeaderReader hdr) {
               headers = hdr.getHeader();
               final String[] rl = getRequestLineArray(hdr.getRequestLine());
               
               httpVersion = rl[0]; //?? wrong get nothing instead, why?
               code = rl[1];
               message = rl[2];
          }          

          private String[] getRequestLineArray(String s) {
               final StringBuilder pt1 = new StringBuilder();
               final StringBuilder pt2 = new StringBuilder();
               final StringBuilder pt3 = new StringBuilder();

               final char[] rl = s.toCharArray();
               byte i = 0;
               for(char c: rl) {

                    if (Character.isWhitespace(c)) {
                         if (i < 2) {
                              i++;
                         }
                         else {
                              // this is if we're @ the last section, if it's a whitespace just append
                              // it. 
                              pt3.append(c);
                         }
                    }
                    else {
                         switch(i) {
                              case 0:
                                   pt1.append(c);
                                break;
                              case 1:
                                   pt2.append(c);
                                break;
                              case 2:
                                   pt3.append(c);
                                break;
                         }
                    }
               }          
               return new String[]{pt1.toString(), pt2.toString(), pt3.toString()};
          }

          @Override
          public String toString()
          {
               final StringBuilder sb = new StringBuilder();

               sb.append(httpVersion).append(" ").append(code).append(" ").append(message).append("\n");
               sb.append("Response Header\n");
               for (StringPair he : headers) {
                    sb.append(he);
                    sb.append("\r\n");
               }
               return sb.toString();
          }
     }
}
