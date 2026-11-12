/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author sedition_act
 */
public class SCookie
{        
     /**
      * Get a string representation of {@code HttpCookie} which can be used in http header.
      * @param cookie
      * @return 
      */
     public static String getCookie(HttpCookie cookie)
     {
          StringBuilder sb = new StringBuilder();
          
          sb.append(cookie.getName()).append("=").append(cookie.getValue());
          
          if (cookie.getDomain() != null)
          {
               sb.append("; ").append("Domain=").append(cookie.getDomain());
          }
          
          if (cookie.getPath() != null)
          {
               sb.append("; ").append("Path=").append(cookie.getPath());
          }
          
          if (cookie.getMaxAge() >= 0)
          {
               // put max age here
               SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
               long exp = System.currentTimeMillis() + cookie.getMaxAge();
               sb.append("; ").append("Expires=").append(sdf.format(new Date(exp))).append(" GMT");
          }
          
          if (cookie.getSecure())
          {
               sb.append("; ").append("Secure");
          }
          
          if (cookie.isHttpOnly())
          {
               sb.append("; ").append("HttpOnly");
          }
          return sb.toString();
     }
}
