/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.StringPair;

/**
 *
 * @author user
 */
public class POSTEntry extends StringPair
{
     private final boolean _valid;

     public POSTEntry(String key, String value)
     {
          super(key, value);
          _valid = true;
     }
     
     /**
      * Create an invalid object (placeholder). 
      * If only key is passed then {@ isValid()} will return false. The value will be an empty 
      * string, the key is the key.
      * @param key 
      */
     public POSTEntry(String key)
     {
          super(key, "");
          _valid = false;
     }
     
      /**
      * Check if this entry is valid or just a placeholder.
      * @return 
      */
     public boolean isValid()
     {
          return _valid;
     }
}
