/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import seditious.code.http.client.ClientException;

import java.net.URI;

// TODO: Belowe
// Why not put separate q string?

/**
 * Class for parsing url.
 * @author sedition_act
 */
public class URLParser 
{
     public URLParser(String url) throws ClientException
     {          
          URI uri = URI.create(url);
          
          scheme = uri.getScheme();
          if (scheme == null)
          {
               // execption
               throw new ClientException(ClientException.Reason.BAD_URL);
          }
          else if (scheme.equalsIgnoreCase("http"))
          {
               if (uri.getPort() < 0)
               {
                    port = 80;
               }
               else
               {
                    port = uri.getPort();
               }
               secure = false;
          }
          else if (scheme.equalsIgnoreCase("https"))
          {
               if (uri.getPort() < 0)
               {
                    port = 443;
               }
               else
               {
                    port = uri.getPort();
               }
               secure = true;
          }
          else
          {
               // exception
               throw new ClientException(ClientException.Reason.BAD_URL);
          }
                    
          host = uri.getHost();
          // read query ...
          final String qs = uri.getRawQuery();
          if (qs == null)
          {
               path = uri.getPath();
          }
          else
          {
               path = uri.getPath() + "?" + uri.getRawQuery();
          }
     }
     
     @Override
     public String toString()
     {
          String rs = host + ", port: " + port;
          if (secure)
          {
               rs = rs + " SECURE connection. ";
          }
          else
          {
               rs = rs + " NON-SECURE connection. ";
          }
          rs = rs + "Path: " + path;
          return rs;
     }
     
     /**
      * Host
      */
     public final String host;
     /**
      * Port
      */
     public final int port;
     /**
      * Secure or unsecure connection (http = {@code false}, https = {@code true})
      */
     public final boolean secure;
     /**
      * Scheme.
      */
     public final String scheme;
     /**
      * Path, plus query string.
      */
     public final String path;
}
