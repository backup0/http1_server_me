/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Class for dealing with url.
 * @author sedition_act
 */
public class URLPath {

     private final ArrayList<String> pathList = new ArrayList<>();
     private final String rawPath;

     public URLPath(String path) throws URISyntaxException {

          rawPath = path;
          path = removeNonPath(path);
          // check if we have a good path .. why?
          // pathString = path;
          // fixme absolute path? Why? Don't do that, what if domain and ?? are not the sayme?
          if (!path.startsWith("/")) {
               path = "/" + path;
          }               
          parse(new URI("http://x.xx" + path).getRawPath());
     }

     private String removeNonPath(String path) {

          final int qmIndex = path.indexOf('?');
          if (qmIndex != -1) {
               return path.substring(0, qmIndex);
          }

          final int hashIndex = path.indexOf('#');
          if (hashIndex != -1) {
               return path.substring(0, hashIndex);
          }
          else {
               return path;
          }
     }

     private void parse(String path) {

          final StringBuilder cp = new StringBuilder();
          for (char c : path.toCharArray()) {
               if (c == '/') {
                    if (cp.length() > 0) {
                         pathList.add(HTTPUtils.decodeParam(cp.toString()));
                         cp.setLength(0);
                    }
               }
               else {
                    cp.append(c);
               }
          }
          
          if (cp.length() > 0) {
               pathList.add(HTTPUtils.decodeParam(cp.toString()));
          }
     }

     /**
      * Get path length. The length is the number of "/" separated parts, not the number of characters.
      * Eg. if the path is "/img/img1.png" this method will return 2.
      * @return Length.
      */
     public int length() {
          return pathList.size();
     }

     /**
      * Get path, from element 0 to element <i>toIndex</i>(exclusive).
      * @param toIndex Last index (exclusive).
      * @return Path.
      */
     public String getTo(int toIndex) {

          final StringBuilder sb = new StringBuilder();
          for (int i = 0; i < toIndex; i++) {
               sb.append("/").append(pathList.get(i));
          }
          return sb.toString();
     }

     /**
      * Get path from <i>startIndex</i>.
      * @param startIndex First element to select (inclusive).
      * @return Path.
      */
     public String getFrom(int startIndex) {

          final StringBuilder sb = new StringBuilder();
          for (int i = startIndex; i < pathList.size(); i++) {
               sb.append("/").append(pathList.get(i));
          }
          return sb.toString();
     }

     /**
      * Get element at <i>index</i> in path.
      * @param index Index.
      * @return Element at index.
      */
     public String getAt(int index) {
          return "/" + pathList.get(index);
     }

     /**
      * Get the full path.
      * @return Full path.
      */
     public String get() {

          final StringBuilder sb = new StringBuilder();
          for (String s : pathList) {
               sb.append("/").append(s);
          }
          return sb.toString();
     }

     @Override
     public String toString() {
          return rawPath;
     }
}
