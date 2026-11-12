/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

/**
 *
 * @author user
 */
public class StringPair
{
     private final String _key;
     private final String _value;
     
     public StringPair(String key, String value)
     {
          _key = key;
          _value = value;
     }
     
     /**
      * Key.
      * @return 
      */
     public String getKey()
     {
          return _key;
     }
     
     /**
      * Value.
      * @return 
      */
     public String getValue()
     {
          return _value;
     }

     @Override
     public String toString()
     {
          return "[" + _key + "=" + _value + "]";
     }
}
