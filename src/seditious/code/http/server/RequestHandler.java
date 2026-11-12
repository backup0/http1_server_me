/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

/**
 * Switcher(?) - when the server receive a request, it will call {@code yo()} which will return a 
 * handler for the requested page (a.k.a resource).
 * 
 * @author user
 */
public interface RequestHandler 
{
     /**
      * Check {@code rqst}, decide which {@code PageHandler} should handle the request, return 
      * a {@code PageHandler}.
      * 
      * @param rqst HTTPRequest instance.
      * @return Page handler to handle this request.
      */
     PageHandler yo(HTTPRequest rqst);
}
