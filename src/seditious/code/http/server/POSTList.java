/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.common.ListHelper;
import seditious.code.http.common.StringPair;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author user
 */
public class POSTList implements Iterable<POSTEntry>
{
     private final ArrayList<POSTEntry> _list;
     
     public POSTList()
     {
          _list = new ArrayList<>();
     }
     
     public POSTList(ArrayList<POSTEntry> entries)
     {
          _list = entries;       
     }
     
     public int size()
     {
          return _list.size();
     }
     
     public boolean hasKey(String key)
     {
          return (new ListHelper(_list)).hasKey(key);
     }
     
     public POSTEntry get(String key)
     {
          StringPair[] entries =  (new ListHelper(_list)).get(key);
          if (entries.length > 0 && entries[0] instanceof POSTEntry)
          {
               return (POSTEntry) entries[0];
          }
          else
          {
               return new POSTEntry(key);
          }
     }
     
     @Override
     public Iterator<POSTEntry> iterator()
     {      
          return _list.iterator();
     }
}
