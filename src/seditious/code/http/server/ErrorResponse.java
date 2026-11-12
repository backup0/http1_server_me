/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.HTTPUtils;

/**
 *
 * @author user
 */
public class ErrorResponse {

     public static HTTPOut getResponse(HTTPStatus status) {
          return getResponse(status, null, "");
     }

     public static HTTPOut getResponse(HTTPStatus status, final String secCode, final String detail) {

          final Page page = new Page(status);
          //page.setResponseLine(status);
          //page.addHeader(new HeaderEntry("Connection", "close"));
          
          page.w("<!DOCTYPE html>");
          page.w("<html>");
          page.w("<head>");
          page.w("<meta charset=\"UTF-8\">");
          page.w("<title>", status.toString(), "</title>");
          page.w("</head>");
          
          page.w("<body>");
          page.w("<h1>", status.getCode() + " ", status.getMessage()  + "</h1>");
          
          page.w("<p>");
          page.w("<span style=\"font-size:large\">Something went wrong, ...");
          page.w("</p>");
          
          page.w("<p>");
          page.w(detail);
          page.w("</p>");
          
          if (secCode != null && secCode.length() > 0) {
               page.w("<p>");
               page.w("[", secCode, "]");
               page.w("</p>");
          }
          
          page.w("<p style=\"font-size:small\">");
          page.w("Serval version ?. ", HTTPUtils.getDate());
          page.w("</p>");
          
          page.w("</body>");
          page.w("</html>");
          
          return page.getHTTPOut();
     }
}


