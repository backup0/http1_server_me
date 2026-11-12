/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import seditious.code.http.exception.BadRequestException;
import seditious.code.http.exception.ServerException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * A class for reading data transferred using chunked transfer encoding.
 * @author sedition_act
 */
public class ChunkReader
{
     public enum Stat {NOT_DONE, OK, SERVER_ERROR, BAD_REQUEST}
     private enum ChunkOp {READ_LENGTH, READ_DATA, READ_DATA_TERMINATOR, READ_TRAILER, NO_OP, X_SERVER, X_REQUEST}

     private static final int CR = 0x0d;
     private static final int LF = 0x0a;

     private final OutputStream outStream;

     private final StringBuilder _textBuffer = new StringBuilder();
     private ChunkOp currentOp = ChunkOp.READ_LENGTH;
     private final ArrayList<byte[]> _buffer = new ArrayList<>();
     private final ArrayList<String> _trailer = new ArrayList<>();
     private ByteArrayOutputStream _bos = new ByteArrayOutputStream();
     private int length = 0;
     private boolean _expectsLF = false;
     private String _errorMsg = "";

     /**
      * Constructor.
      * @param os Output steam to write data to.
      */
     public ChunkReader(OutputStream os)
     {
         outStream = os;   
     }

     public void addData(byte[] b) {

          // let's break up ..
          // todo: split so one block won't be too large - but not too little.
          _buffer.add(b);
     }

     public void update() {
          if (_buffer.isEmpty()) {
               return;
          }

          try {
               execute();
          }
          catch (BadRequestException e) {
               currentOp = ChunkOp.X_REQUEST;
               _errorMsg = e.getMessage();
          }
          catch (Exception e) {
               currentOp = ChunkOp.X_SERVER;
               _errorMsg = e.getMessage();
          }
     }

     private void execute() throws IOException, BadRequestException, ServerException {

          final ByteArrayInputStream bis;
          bis = new ByteArrayInputStream( _buffer.remove(0));

          while(bis.available() > 0) {

               switch (currentOp) {
                    case READ_LENGTH:
                         readLength(bis.read());
                         break;
                    case READ_DATA:
                         // it'll read whatever amount of data, up to the length of the ?? remaining.
                         readData(bis.readNBytes(length));
                         break;
                    case READ_TRAILER:
                         readTrailer(bis.read());
                         if (currentOp == ChunkOp.NO_OP) {
                              _bos.write(bis.readAllBytes());
                              doTheRest();
                         }
                         break;
                    case READ_DATA_TERMINATOR:
                         readDataTerminator(bis.read());
                         break;
                    default:
                         throw new ServerException();
               }
          }
     }

     private void readLength(int i) throws IOException, BadRequestException {

          if (i == -1) {
               throw new IOException("EOS");
          }

          // we expect lf - but we get something else.
          if (_expectsLF && i != LF) {
               throw new BadRequestException();
          }

          switch (i) {
               case CR:
                    // do nothing ... either cr+lf or lf, lf is the end.
                    _expectsLF = true;
                    break;
               case LF:
                    // LF is the end
                    _expectsLF = false;
                    processDataLength();
                    break;
               default:
                    _textBuffer.append((char) i);
                    _expectsLF = false;
                    break;
          }
     }

     // Process the length segment here.
     // length found - read.
     private void processDataLength() throws BadRequestException {
          final StringBuilder bs = new StringBuilder();

          for (int i = 0; i < _textBuffer.length(); i++) {
               final char c = _textBuffer.charAt(i);
               if (c == ';') {
                    break;
               }
               else {
                    bs.append(c);
               }
          }
          _textBuffer.setLength(0);

          try {
               length = Integer.parseInt(bs.toString(), 16);
          }
          catch (Exception e) {
               throw new BadRequestException();
          }

          if (length == 0) {
               // last data block ..
               // there's no data block ro read, there's no data terminator
               currentOp = ChunkOp.READ_TRAILER;
          }
          else {
               currentOp = ChunkOp.READ_DATA;
          }
     }

     private void readData(byte[] bs) throws IOException {

          outStream.write(bs);
          length = length - bs.length;

          if (length == 0) {
               currentOp = ChunkOp.READ_DATA_TERMINATOR;
          }
     }

     private void readDataTerminator(int i) throws IOException, BadRequestException {

          if (i == -1) {
               throw new IOException("EOS");
          }

          if (_expectsLF && i != LF) {
               throw new BadRequestException();
          }

          switch (i) {
               case CR:
                    // do nothing ... either cr+lf or lf, lf is the end.
                    _expectsLF = true;
                    break;
               case LF:
                    // LF is the end
                    _expectsLF = false;
                    currentOp = ChunkOp.READ_LENGTH;
                    break;
               default:
                    throw new BadRequestException();
          }
     }

     private void readTrailer(int i) throws IOException, BadRequestException {

          if (i == -1) {
               throw new IOException("EOS");
          }

          // we expect lf - but we get something else.
          if (_expectsLF && i != LF) {
               throw new BadRequestException();
          }

          switch (i) {
               case CR:
                    // do nothing ... either cr+lf or lf, lf is the end.
                    _expectsLF = true;
                    break;
               case LF:
                    // LF is the end
                    _expectsLF = false;
                    procTrailerLF();
                    break;
               default:
                    _textBuffer.append((char) i);
                    _expectsLF = false;
                    break;
          }
     }

     private void procTrailerLF() {

          if (_textBuffer.length() == 0) {
               // done.
               // the last item here.
               currentOp = ChunkOp.NO_OP;
               return;
          }

          _trailer.add(_textBuffer.toString());
          _textBuffer.setLength(0);
     }

     private void doTheRest() throws IOException {

          while (!_buffer.isEmpty()) {
               _bos.write(_buffer.remove(0));
          }
     }

     public byte[] getRemainder() {
          return _bos.toByteArray();
     }

     public List<String> getTrailers() {
          return _trailer;
     }

     public Stat getStat() {

          final Stat stat;

          switch (currentOp) {
               case READ_LENGTH:
               case READ_DATA:
               case READ_TRAILER:
               case READ_DATA_TERMINATOR:
                    stat = Stat.NOT_DONE;
                    break;
               case NO_OP:
                    stat = Stat.OK;
                    break;
               case X_REQUEST:
                    stat = Stat.BAD_REQUEST;
                    break;
               case X_SERVER:
               default:
                    stat = Stat.SERVER_ERROR;
                    break;
          }
          return stat;
     }

     public String getErrorMsg() {
          return _errorMsg;
     }
}   
     
