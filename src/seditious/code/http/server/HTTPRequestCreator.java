/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public class HTTPRequestCreator {
     
     enum Stat {
          OK, BAD_REQUEST, SERVER_ERROR
     }
     
     private final HTTPMethods method;
     private final URLPath path;
     private final HTTPVersions version;

     private final List<StringPair> headers;
     private final Map<String, String> queries;
     
     private Stat stat = Stat.OK;     

     public HTTPRequestCreator (HeaderReader hdr) {

          HTTPMethods method;
          URLPath path;
          HTTPVersions version;
          List<StringPair> headers;
          Map<String, String> queries;
          
          try {
               final String[] rlp = parseRequestLine(hdr.getRequestLine());
               method = getMethod(rlp[0]);
               path = new URLPath(rlp[1]);
               version = getVersion(rlp[2]);
               queries = getQueries(rlp[1]);
               headers = hdr.getHeader();
          }
          catch (Exception ignored) {

               method = null;
               path = null;
               version = null;
               queries = null;
               headers = null;
               
               if (stat == Stat.OK) {
                    stat = Stat.SERVER_ERROR;
               }
          }

          this.method = method;
          this.path = path;
          this.version = version;
          this.queries = queries;
          this.headers = headers;
     }

     private String[] parseRequestLine(String s) {

          final StringBuilder method = new StringBuilder();
          final StringBuilder path = new StringBuilder();
          final StringBuilder version = new StringBuilder();

          final ArrayList<Character> charList = new ArrayList<>();

          for (char c : s.toCharArray()) {
               charList.add(c);
          }

          char tempChar;

          // the method part
          while (!charList.isEmpty()) {
               tempChar = charList.remove(0);
               if (Character.isWhitespace(tempChar)) {
                    break;
               }
               else {
                    method.append(tempChar);
               }
          }

          // the end part
          while (!charList.isEmpty()) {
               tempChar = charList.remove(charList.size() - 1);
               if (Character.isWhitespace(tempChar)) {
                    break;
               }
               else {
                    version.insert(0, tempChar);
               }
          }

          // the rest
          while (!charList.isEmpty()) {
               path.append(charList.remove(0));
          }
          return new String[]{method.toString(), path.toString(), version.toString()};
     }

     private HTTPMethods getMethod(String s) {

          final HTTPMethods method;

          switch(s) {

               case "GET":
                    method = HTTPMethods.GET;
                    break;
               case "OPTIONS":
                    method = HTTPMethods.OPTIONS;
                    break;
               case "HEAD":
                    method = HTTPMethods.HEAD;
                    break;
               case "POST":
                    method = HTTPMethods.POST;
                    break;
               case "PUT":
                    method = HTTPMethods.PUT;
                    break;
               case "PATCH":
                    method = HTTPMethods.PATCH;
                    break;
               case "DELETE":
                    method = HTTPMethods.DELETE;
                    break;
               case "TRACE":
                    method = HTTPMethods.TRACE;
                    break;
               case "CONNECT":
                    method = HTTPMethods.CONNECT;
                    break;
               default:
                    method = null;
                    stat = Stat.BAD_REQUEST;
          }
          return method;
     }

     private HTTPVersions getVersion(String s) {

          final HTTPVersions version;
          // no null pointer because if version is null exception will have been thrown
          switch (s) {
               case "0.9":
                    version = HTTPVersions.v09;
                    break;
               case "1.0":
                    version = HTTPVersions.v10;
                    break;
               case "1.1":
                    version = HTTPVersions.v11;
                    break;
               case "2.0":
                    version = HTTPVersions.v20;
                    break;
               default:
                    version = HTTPVersions.v00;
                    break;
          }
          return version;
     }

     private Map<String, String> getQueries(String s) throws Exception {

          if (s.length() == 0) {
               stat = Stat.BAD_REQUEST;
               throw new Exception();
          }

          final String pathSansFragment;
          final int indexOfHash = s.lastIndexOf('#');
          if (indexOfHash != -1) {
               pathSansFragment = s.substring(0, indexOfHash);
          }
          else {
               pathSansFragment = s;
          }

          final String query;
          final int indexOfQ = pathSansFragment.indexOf('?');
          if (indexOfQ != -1) {
               query = pathSansFragment.substring(indexOfQ + 1);
          }
          else {
               query = "";
          }
          return HTTPUtils.readQueryString(query);
     }

     public Stat getStat() {
          return stat;
     }

     public HTTPRequest getRequest() {
          return new HTTPRequest(method, path, version, headers, queries);
     }
}
