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
public abstract class HTTPStatus
{
     public abstract int getCode();
     public abstract String getMessage();
     
     @Override
     public String toString()
     {
          return getCode() + " " + getMessage();
     }
     
     /**
      * OK.
      */
     public static class HTTP200 extends HTTPStatus
     {
          @Override
          public int getCode()
          {
               return 200;
          }

          @Override
          public String getMessage()
          {
               return "OK";
          }
     
     }
     
     /**
      * Bad request.
      */
     public static class HTTP400 extends HTTPStatus
     {
          @Override
          public int getCode()
          {
               return 404;
          }

          @Override
          public String getMessage()
          {
               return "Bad Request";
          }     
     }
     
     /**
      * Not found.
      */
     public static class HTTP404 extends HTTPStatus
     {
          @Override
          public int getCode()
          {
               return 404;
          }

          @Override
          public String getMessage()
          {
               return "Not Found";
          }     
     }
     
     /**
      * Internal server error.
      */
     public static class HTTP500 extends HTTPStatus
     {
          @Override
          public int getCode()
          {
               return 500;
          }

          @Override
          public String getMessage()
          {
               return "Internal Server Error";
          }     
     }
     
     /**
      * Not implemented.
      */
     public static class HTTP501 extends HTTPStatus
     {
          @Override
          public int getCode()
          {
               return 501;
          }

          @Override
          public String getMessage()
          {
               return "Not Implemented";
          }     
     }
}
