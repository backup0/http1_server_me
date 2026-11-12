/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.HeaderReader;

import java.util.Arrays;

/**
 *
 * @author user
 */
public class HeaderProx {

     private final HeaderReader headerReader;

     private HTTPRequest request = null;
     private byte[] remainder = new byte[0];
     private String error = "";
     private Stat stat = Stat.NOT_FOUND_YET;

     public enum Stat {
          NOT_FOUND_YET,
          BAD_HEADER,
          OK,
          BAD_REQUEST,
          SERVER_ERROR
     }

     public HeaderProx() {
          headerReader = new HeaderReader();
     }

     public void update(byte[] b) {

          if (stat != Stat.NOT_FOUND_YET) {
               return;
          }

          HeaderReader.Status hrStat = HeaderReader.Status.NOT_FOUND_YET;
          int i = 0;
          for (; i < b.length; i++) {
               hrStat = headerReader.update(b[i]);
               if (hrStat != HeaderReader.Status.NOT_FOUND_YET) {
                    // as long we we have not found, we keep going; until we run out of data.
                    break;
               }
          }

          switch (hrStat) {
               case NOT_FOUND_YET:
                    // we've read every byte supplied and have not read the entire header
                    // we're not done yet.
                    break;
               case MALFORMED:
                    // header malformed
                    // bad stuff.
                    stat = Stat.BAD_HEADER;
                    break;
               //case FOUND:
               case DONE:
               default:
                    // good, time to process ...
                    // process the remainder.
                    stat = createRequest();
                    if (stat == Stat.OK) {
                         remainder = Arrays.copyOfRange(b, i, b.length);
                    }
                    break;
          }
     }

     private Stat createRequest() {

          final Stat s;
          final HTTPRequestCreator rc = new HTTPRequestCreator(headerReader);

          switch (rc.getStat()) {
               case OK:
                    s = Stat.OK;
                    request = rc.getRequest();
                    break;
               case BAD_REQUEST:
                    s = Stat.BAD_REQUEST;
                    error = "Bad request.";
                    break;
               case SERVER_ERROR:
               default:
                    s = Stat.SERVER_ERROR;
                    error = "Server error";
                    break;
          }
          return s;
     }

     public Stat getStat () {
          return stat;
     }

     public HTTPRequest getRequest() {
          return request;
     }

     public byte[] getRemainder() {
          return remainder;
     }

     public String getErrorMsg() {
          return error;
     }
}
