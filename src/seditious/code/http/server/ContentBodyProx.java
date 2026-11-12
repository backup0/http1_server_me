/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.*;
import seditious.code.http.exception.ServerException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Content body processor. Takes bytes. Write to OutputStream, then either to
 * a file or to ?.
 * @author user
 */
public class ContentBodyProx {

     public enum Stat {
          NOT_DONE_YET,
          OK,
          ERROR
     }

     private final PageHandler pageHandler;
     private final OutputStream outStream;
     private final int chunkedMagixNumber = -69;
     private final boolean formData;
     private Map<String, String> POSTEntries = new HashMap<>();
     private final List<Path> filePaths = new ArrayList<>();

     private long length;
     private final ChunkReader chunkReader;

     private byte[] remainder = new byte[0];
     private Path filePath = null;
     private String errorMsg = "";
     private Stat stat = Stat.NOT_DONE_YET;

     public ContentBodyProx(Path tempFileDir, List<StringPair> hdr, PageHandler ph, boolean byLen)
     {
          pageHandler = ph;
          length = byLen ? getLength(hdr) : chunkedMagixNumber;
          formData = isFormData(hdr);

          OutputStream os;
          try {
               if (formData) {
                    os = new ByteArrayOutputStream();
               }
               else {
                    os = getOutputStream(tempFileDir);
               }
          }
          catch (Exception e) {
               // set something.
               os = new DummyOutputStream();
               cleanUpAfterFailure(e.getMessage());
          }
          // if op failed (exception throw), flag is set to ERROR,
          // that's it. We assign output stream anyway - why not?
          outStream = os;
          // yes we may not need to use it, but create anyway otherwise will have to check @ each ??
          chunkReader = new ChunkReader(outStream);
     }

     private long getLength(List<StringPair> hdr) {

          final String[] length = ListHelper.getValues(Headers.CONTENT_LENGTH, hdr);
          return Long.parseLong(length[0]);
     }

     private boolean isFormData(List<StringPair> hdr) {

          final String[] cType = ListHelper.getValues(Headers.CONTENT_TYPE, hdr);
          String value = "";
          if (cType.length > 0) {
               value = cType[0];
          }
          return "application/x-www-form-urlencoded".equalsIgnoreCase(value);
     }

     private OutputStream getOutputStream(Path tempFileDir) throws Exception {

          // get output stream from 'user'
          final OutputStream outs = pageHandler.in();
          if (outs == null) {

               // null we decide..
               // store file somewhere - later tell the user where
               final Path f = createTempFile(tempFileDir);
               filePaths.add(f);
               return new BufferedOutputStream(new FileOutputStream(f.toFile()));
          }
          else {
               return outs;
          }
     }
     
     private Path createTempFile(Path tempFileDir) throws Exception
     {
          if (tempFileDir == null) {
               throw new ServerException("Temp dir 404.");
          }

          Path p = null;
          boolean tryAgain = true;
          int count = 0;

          while (tryAgain) {
               try {
                    p = tempFileDir.resolve("file_" + HTTPUtils.getUnsecRandString(20));
                    // then create the file ..
                    Files.createFile(p);
                    tryAgain = false;
               }
               catch (FileAlreadyExistsException fae) {
                    tryAgain = true;
                    if (++count > 250) {
                         p = null;
                         break;
                    }
               } 
               catch (IOException ex) {
                    tryAgain = false;
                    p = null;
               }
          }  

          if (p == null) {
               throw new ServerException("Can't create temporary file.");
          }
          else {
               return p;
          }
     }

     private void getFormData() throws Exception {

          final String s;
          s = ((ByteArrayOutputStream) outStream).toString(StandardCharsets.US_ASCII.name());
          POSTEntries = HTTPUtils.readQueryString(s);
     }

     public void write(byte[] data) {

          if (stat != Stat.NOT_DONE_YET) {
               return;
          }

          try {
               if (length == chunkedMagixNumber) {
                    readData_chunked(data);
               }
               else {
                    readData_length(data);
               }

               if (stat == Stat.OK) {
                    // read len or read chunk will set the flag, stat flag.
                    if (formData) {
                         getFormData();
                    }

                    pageHandler.doneIn(outStream, true);
               }
          }
          catch (Exception e) {
               cleanUpAfterFailure(e.getMessage());
          }
     }

     private void cleanUpAfterFailure(String errorMsg) {

          // maybe it has been created
          while (!filePaths.isEmpty()) {
               try {
                    final Path p = filePaths.remove(0);
                    Files.deleteIfExists(p);

               } catch (Exception ignored) {}
          }
          // notify handler.
          pageHandler.doneIn(outStream, false);
          stat = Stat.ERROR;
          this.errorMsg = errorMsg;
     }

     private void readData_chunked(byte[] data) throws Exception {

          chunkReader.addData(data);
          chunkReader.update();

          switch (chunkReader.getStat()) {
               case NOT_DONE:
                    break;
               case OK:
                    stat = Stat.OK;
                    remainder = chunkReader.getRemainder();
                    break;
               case BAD_REQUEST:
               case SERVER_ERROR:
               default:
                    throw new Exception();
          }
     }

     private void readData_length(byte[] data) throws Exception {

          final ByteArrayInputStream bis = new ByteArrayInputStream(data);
          final long toReadLen = Long.min(data.length, length);

          final byte[] b = bis.readNBytes((int) toReadLen);
          outStream.write(b);
          length = length - toReadLen;

          if (length <= 0) {
               // read the remainder, if there's any.
               remainder = bis.readAllBytes();
               stat = Stat.OK;
          }
     }

     public Stat getStat() {
          return stat;
     }

     public byte[] getRemainder() {
          return remainder;
     }

     public String getErrorMsg() {
          return errorMsg;
     }

     public Map<String, String> getPOSTData() {
          return POSTEntries;
     }

     public List<Path> getFiles() {
          return filePaths;
     }
}
