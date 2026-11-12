/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.HTTPMethods;
import seditious.code.http.common.HTTPVersions;
import seditious.code.http.common.StringPair;
import seditious.code.http.common.URLPath;

import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public class HTTPRequest
{
     private final HTTPMethods method;
     private final URLPath path;
     private final HTTPVersions version;
     private final List<StringPair> headers;
     private final Map<String, String> queries;

     public HTTPRequest(HTTPMethods method, URLPath path, HTTPVersions version, List<StringPair> headers,
          Map<String, String> queries) {
          
          this.method = method;
          this.path = path;
          this.version = version;
          this.queries = queries;
          this.headers = headers;
     }

     public HTTPVersions getVersion() {
          return version;
     }
             
     /**
      * Get request method.
      * @return
      */
     public HTTPMethods getMethod() {
          return method;
     }
     
     /**
      * Get path requested.
      * @return
      */
     public URLPath getPath() {
          return path;
     }
     
     /**
      * Get headers.
      * @return
      */
     public List<StringPair> getHeaders() {
          return headers;
     }

     public Map<String, String> getQueries() {
          return queries;
     }
}
