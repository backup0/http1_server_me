/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import java.nio.file.Path;

/**
 *
 * @author user
 */
public class FileEntry
{
     public final String name;
     public final Path path;

     public FileEntry(String name, Path path)
     {
          this.name = name;
          this.path = path;
     }
     
     @Override
     public String toString()
     {
          return name + "=" + path.toString();
     }
}