/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Stuff that don;t fit anywhere else.
 * @author sedition_act
 */
public class HTTPUtils 
{     
     /**
      * Get current date/time in <code>EEE, dd MMM yyyy HH:mm:ss GMT</code> format.
      * @return
      */
     public static String getDate()
     {
          SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
          return sdf.format(new Date()) + "GMT";
     }
     
     /**
      * Decode the string.
      * @param sp
      * @return Decoded string.
      */
     public static String decodeParam(String sp)
     {
          try 
          {
               return URLDecoder.decode(sp, StandardCharsets.UTF_8.toString());
          } 
          catch (UnsupportedEncodingException exp) 
          {
               return "";
          }
     }
     
     /**
      * Encode the string using percent encoding.
      * @param s
      * @param plus Whether to use + instead of %20 to encode space. If this is true + is used.
      * @return Percent encoded string.
      */
     public static String encodeParam(String s, boolean plus)
     {
          String rtn;
          
          try 
          {
               rtn = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
               
               if (!plus)
               {
                    rtn = rtn.replace("+", "%20");
               }
               
          } catch (UnsupportedEncodingException ex) 
          {
               rtn = null;
          }
     
          return rtn;
     }
     
     /**
      * Parse query string.
      * @param s
      * @return 
      */
     public static Map<String, String> readQueryString(String s)
     {
          // todo: correct map?
          HashMap<String, String> hm = new HashMap<>();
          String[] part = s.split("&");
          String ts, key, value;
          int i = 0, eIndex;
          
          while(i < part.length)
          {
               ts = part[i];
               if (ts.length() > 0)
               {
                    eIndex = ts.indexOf("=");
                    if (eIndex > 0)
                    {
                         key = decodeParam(ts.substring(0, eIndex));
                         value = decodeParam(ts.substring(eIndex + 1));
                         if (key.length() > 0)
                         {
                              hm.put(key, value);
                         }
                    }
               }
               i++;
          }
          return hm;
     }
     
     
     /**
      * Create query string using values from the <code>HashMap</code>.
      * @param params
      * @param usePlus Whether to use + instead of %20 to encode space. If this is true + is used.
      * @return 
      */
     public static String buildQueryString(Map<String, String> params, boolean usePlus)
     {
          StringBuilder sb = new StringBuilder();
          
          for (Entry<String, String> e : params.entrySet())
          {
              if (e.getValue() != null)
              {
                   sb.append("&").append(encodeParam(e.getKey(), usePlus)).append("=").
                           append(encodeParam(e.getValue(), usePlus));
              }
              else
              {
                   sb.append("&").append(encodeParam(e.getKey(), usePlus)).append("=");
              }
          }
          
          String rv = sb.toString();
          if (rv.length() > 0)
          {
               return rv.substring(1);
          }          
          else
          {
               return "";
          }
     }
          
     /**
      * Get random string (between A - Z, a - z, 0 - 9) generated using <code>java.util.Random</code>
      * @param len The length of the strign to generate.
      * @return 
      */
     public static String getUnsecRandString(int len)
     {         
          StringBuilder sb = new StringBuilder();
          int i = 0, max = uchars.length, n;     
          
          while (i < len)
          {
               n = urand.nextInt(max);
               if (n >= max)
               {
                    continue;
               }
               sb.append(uchars[n]);
               i++;
          }         
          
          return sb.toString();
     }
     
     /**
      * Get random string (between A - Z, a - z, 0 - 9) using secure random number generator.
      * @param len The length of the string to generate.
      * @return 
      */
     public static String getSecRandString(int len)
     {
          byte[] ps = new byte[len];
          srand.nextBytes(ps);
          
          StringBuilder sb = new StringBuilder();
          int o = 0;
          
          while (o < ps.length)
          {
               if (ps[o] < 0)
               {
                    sb.append(schars[ps[o] * - 1]);
               }
               else
               {
                    sb.append(schars[ps[o]]);
               }
               o++;
          }
          return sb.toString();
     }
     
     static final char[] uchars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
     static final Random urand = new Random();
     
     static final char[] schars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZmyLC0".toCharArray();
     static final SecureRandom srand = new SecureRandom();
}
