/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.util.ArrayList;
import java.util.List;

/**
 * A list for holding {@code HeaderEntry}.
 * @author sedition_act
 */
public class ListHelper
{
     /**
      * Create a list.
      * @param list
      */
     public ListHelper(ArrayList<?> list)
     {
           _list = list;
     } 
     
     /**
      * Check if the list contains the {@code key}.
      * @param key
      * @return 
      */
     public boolean hasKeyIgnoreCase(String key)
     {
          boolean has = false;
          for (Object sp : _list)
          {
               if (sp instanceof StringPair)
               {
                    if (key.equalsIgnoreCase(((StringPair)sp).getKey()))
                    {
                         has = true; break;
                    }
               }
          }
          return has;
     }
     
     /**
      * Check if the list contains the {@code key}.
      * @param key
      * @return 
      */
     public boolean hasKey(String key)
     {
          boolean has = false;
          for (Object sp : _list)
          {
               if (sp instanceof StringPair)
               {
                    if (key.equals(((StringPair) sp).getKey()))
                    {
                         has = true; break;
                    }
               }
          }
          return has;
     }
     
     /**
      * Get the header associated with the {@code key}.
      * @param key The key.
      * @return An array containing {@code HeaderEntry} instances associated with the key, or a blank array.
      */
     public StringPair[] getIgnoreCase(String key)
     {
          ArrayList<StringPair> result = new ArrayList<>();
          
          for (Object obj : _list)
          {
               if (obj instanceof StringPair)
               {
                    StringPair sp = (StringPair) obj;
                    if (key.equalsIgnoreCase(sp.getKey()))
                    {
                         result.add(sp);
                    }
               }
          }                    
          return result.toArray(new StringPair[result.size()]);
     }
     
     /**
      * Get the header associated with the {@code key}.
      * @param key The key.
      * @return An array containing {@code HeaderEntry} instances associated with the key, or a blank array.
      */
     public StringPair[] get(String key)
     {
          ArrayList<StringPair> result = new ArrayList<>();
          
          for (Object obj : _list)
          {
               if (obj instanceof StringPair)
               {
                    StringPair sp = (StringPair) obj;
                    if (key.equals(sp.getKey()))
                    {
                         result.add(sp);
                    }
               }
          }                    
          return result.toArray(new StringPair[result.size()]);
     }

     public static String[] getValues(String key, List<StringPair> list) {

          final ArrayList<String> result = new ArrayList<>();
          for (StringPair sp : list) {
               if (key.equalsIgnoreCase(sp.getKey())) {
                    result.add(sp.getValue());
               }
          }
          return result.toArray(new String[0]);
     }

     public static boolean hasKey(String key, List<StringPair> list) {

          boolean hasKey = false;
          for (StringPair sp : list)
          {
               if (key.equalsIgnoreCase(sp.getKey()))
               {
                    hasKey = true; break;
               }
          }
          return hasKey;
     }

     private final ArrayList<?> _list;
}
