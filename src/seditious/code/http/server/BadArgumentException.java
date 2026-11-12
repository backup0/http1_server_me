/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

import seditious.code.http.exception.ServerException;

/**
 *
 * @author user
 */
public class BadArgumentException extends ServerException
{
     public BadArgumentException()
     {
          this("BadArgumentException");
     }
      
     public BadArgumentException(String msg)
     {
          super(msg);
     }
     
}
