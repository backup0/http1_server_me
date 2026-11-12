/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import java.net.InetAddress;
import java.nio.file.Path;

/**
 *
 * @author user
 */
public class ServerSettings {

     private InetAddress address;
     private final String[] keyStoreInfo = new String[2];
     // todo: what? Port we have 2.
     private final int[] ports = new int[]{80, 443};
     private RequestHandler requestHandler = null;
     private long connTimeout = 16000;
     private long sleepTimeout = 500;
     private Path tempDir = null;
     private XLogger logger = new XLogger();

     public ServerSettings() {
          try {
               address = InetAddress.getLocalHost();
          }
          catch (Exception e) {
               address = null;
          }
     }

     public void setConnectionTimeout(long l) {
          connTimeout = l;
     }
     
     public long getConnectionTimeout() {
          return connTimeout;
     }

     public void setSleepTimeout(long l) {
          sleepTimeout = l;
     }

     public long getSleepTimeout() {
          return sleepTimeout;
     }

     public void setAddress(InetAddress ip) {
          address = ip;
     }

     /**
      * Get address.
      * @return 
      */
     public InetAddress getAddress() {
          if (address == null) {
               try {
                    return InetAddress.getLocalHost();
               }
               catch (Exception e) {
                    return null;
               }
          }
          else {
               return address;
          }
     }

     public void setPorts(int unsec, int sec) {
          ports[0] = unsec; ports[1] = sec;
     }

     /**
      * Get port.
      * #0 - unsecure, #1 - secure; default 80/443.
      * @return Ports.
      */
     public int[] getPorts()
     {
          return ports;
     }
     
     public void setRequestHandler(RequestHandler rh) {
          requestHandler = rh;
     }
     
     /**
      * Get handler.
      * @return 
      */
     public RequestHandler getRequestHandler() {
          return requestHandler;
     }
     
     public void setTempDir(Path p) {
          tempDir = p;
     }
     
     /**
      * Get temporary directory. 
      * @return
      */
     public Path getTempDir() {
          return tempDir;
     }
     
     public void setLogger(XLogger logger) {
          this.logger = logger;
     }
     
     /**
      * Get logger.
      * @return 
      */
     public XLogger getLogger() {
          return logger;
     }

     public void setKeyStore(String loc, String pass) {
          keyStoreInfo[0] = loc; keyStoreInfo[1] = pass;
     }

     /**
      * #0 - location, #1 - password.
      * @return
      */
     public String[] getKeyStore() {
          return keyStoreInfo;
     }


     public void notifyAboutClosing() {

     }
}
