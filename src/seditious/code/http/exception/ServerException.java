/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.exception;

/**
 *
 * @author user
 */
public class ServerException extends Exception
{
     public ServerException(String msg)
     {
          super(msg);
     }

     public ServerException() {
          this("Server exception");
     }
}
