/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.Headers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * This is a page. Something like a page. Representation of an html page? 
 * This is convenience class, inherit, or instantiate
 * you won't have to set up too many things.
 * 
 * @author sedition_act
 */
public class Page {

     private final HTTPOut httpOut = new HTTPOut();
     private final StringBuilder textStore = new StringBuilder();

     public Page() {}

     public Page(HTTPStatus status) {
          httpOut.setStatus(status);
     }

     /**
      * Add text.
      * @param text Text.
      */
     public final void w(String... text) {

          for (String txt : text) {
               textStore.append(txt);
          }
     }

     /**
      * Insert opening tag. The first item is the tag. The 2nd and subsequent items are attributes. Attribute format:
      * name=value, '"' will be added. Eg.
      * <pre>tag("a", "href=abc.com")</pre>
      * this will produce
      * <pre>&lt;a href="abc.com"&gt;</a></pre>
      * @param txt Tag + attributes.
      */
     public void tag(String... txt) {

          textStore.append(" <");
          textStore.append(txt[0]);

          for (int i = 1; i < txt.length; i++) {

               final String s = txt[i].replaceFirst("=", "=\"");
               textStore.append(" ");
               textStore.append(s);
               textStore.append("\"");
          }
          textStore.append(">");
     }

     /**
      * Get http out. Pass this to the server through PageHandler. Content-Type and Content-Length 
      * will be set automatically.
      * @return HTTPOut instance,
      */
     public HTTPOut getHTTPOut() {
          final byte[] b = textStore.toString().getBytes(StandardCharsets.UTF_8);
          httpOut.setData(new ByteArrayInputStream(b));
          httpOut.setOption(Headers.CONTENT_LENGTH, b.length + "");
          httpOut.setOption(Headers.CONTENT_TYPE, "text/html; charset=utf-8");
          return httpOut;
     }

     public String toString() {
          return textStore.toString();
     }
}
