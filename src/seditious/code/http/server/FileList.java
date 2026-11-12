/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author user
 */
public class FileList implements Iterable<FileEntry>
{     
     private final ArrayList<FileEntry> _entries; 
     
     public FileList()
     {
          this(new ArrayList<FileEntry>());
     }
     
     public FileList(ArrayList<FileEntry> files)
     {
          _entries = files;
     }

     @Override
     public Iterator<FileEntry> iterator()
     {
          return _entries.iterator();
     }
}
