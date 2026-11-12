/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.Main;

/**
 *
 * @author user
 */
public class XLogger
{
     /**
      * Log.
      * @param message 
      */
     public void log(String message) {
          Main.w(message);
     }
}
