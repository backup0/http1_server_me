/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Dec 19, 2017
 * Replace HeaderList with new HeaderList(Immutable)
 * Rename globals, add _.
 * 
 * Dec 10, 2016 
 * Strip white space from request line.
 */

// TODO: What if header value has mutiple white space, comparing value can be tricky, but can't just 
// 'em, why .. What if the white space is part of the value, and not just extra, say boundary ...
// need to check quote as well, see how things can get complicated very quickly.
package seditious.code.http.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 2016-09-11
 * Add if (status != done) @ start of some functions, so that if those are called on undone header
 * exception isn't thrown.
 */

/**
 * A class for parsing http header.
 * @author sedition_act
 */
public final class HeaderReader 
{
     private String _requestLine;

     private final ArrayList<StringPair> _hdr;

     public enum Status {NOT_FOUND_YET, DONE, MALFORMED}
     private Status status = Status.NOT_FOUND_YET;

     final StringBuilder _buffer = new StringBuilder();
     boolean _expectsLF = false;

     public HeaderReader() {
          _requestLine = null;
          _hdr = new ArrayList<>();
     }
     
     public Status update(byte data) {
          return execUpdate(data);
     }
     
     /**
      * Add data to the reader.
      * @param i
      * @return 
      */
     public Status update(int i) {
          byte b;
          if (i > 127) {
               b = (byte) - (256 - i);
          }
          else {
               b = (byte) i;
          }
          return execUpdate(b);
     }

     private Status execUpdate(byte x) {

          if (status != Status.NOT_FOUND_YET) {
               return status;
          }

          final byte CR = 0x0d;
          final byte LF = 0x0a;

          // we expect lf - but we get something else.
          if (_expectsLF && x != LF) {
               status = Status.MALFORMED;
               return status;
          }

          switch (x) {
               case CR:
                    // do nothing ... either cr+lf or lf, lf is the end.
                    //status = Status.NOT_FOUND_YET;
                    _expectsLF = true;
                    break;
               case LF:
                    // LF is the end
                    _expectsLF = false;
                    status = procLF();
                    break;
               default:
                    _buffer.append((char) x);
                    _expectsLF = false;
                    status = Status.NOT_FOUND_YET;
                    break;
          }
          return status;
     }
     
     private Status procLF() {
          // line feed .. end of the line, we're good.
          final Status stat;
          // empty line
          if (_buffer.length() == 0) {
               // + empty request line.
               if (_requestLine == null) {
                    stat = Status.NOT_FOUND_YET;
               }
               else {
                    // no need to store the item.
                    stat = Status.DONE;
               }
          }
          else {
               // length is not 0
               if (_requestLine == null) {
                    // the first item is the request/status line
                    _requestLine = _buffer.toString();
               }
               else {
                    _hdr.add(parseHeaderField());
               }
               stat = Status.NOT_FOUND_YET;
               // reset the buffer
               _buffer.setLength(0);
          }
          return stat;
     }
     
     private StringPair parseHeaderField() {
          // todo: more robustness...
          final String s = _buffer.toString();
          final int cIndex = s.indexOf(":");
          if (cIndex == -1) {
               // todo: is this ok?
               return new StringPair(s, "");
          }
          else {
               final String fuk = s.substring(cIndex + 1).trim();
               return new StringPair(s.substring(0, cIndex).trim(), fuk);
          }
     }

     /**
      * Get the request line.
      * @return Request line, or empty string is the header is not fully read.  
      */
     public String getRequestLine() {
          if (status != Status.DONE) {
               return "";
          }
          
          return _requestLine;
     }
     
     public Status getStatus() {
          return status;
     }
     
     public List<StringPair> getHeader() {
          return _hdr;
     }

     public String toString () {
          return _requestLine + "; " + Arrays.toString(_hdr.toArray(new Object[0]));
     }
}
