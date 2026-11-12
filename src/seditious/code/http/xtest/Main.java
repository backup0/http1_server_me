package seditious.code.http.xtest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import seditious.code.http.server.HTTPServer;
import seditious.code.http.server.ServerSettings.ServerSettingsCreator;

import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class Main {

     private void  doSomething() throws Exception
     {    
          startServer();
     }
     
     private boolean startServer = true;
     private void startServer() throws Exception
     {
          if (startServer)
          {
               ServerSettingsCreator ss = new ServerSettingsCreator();
               ss.setPort(80);
               ss.setTempDir(Paths.get("D:\\Download\\servals\\temp"));
               ss.setTimeOut(15000);
               //ss.setRequestHandler(new ErmuzHandler());
               ss.setRequestHandler(new MuzHandler());
               //ss.setRequestHandler(new Handlers.RH());
               //ss.setRequestHandler(new Handlers.Ex());
               //ss.setRequestHandler(new Handlers.RHNull());
               
               HTTPServer svr = new HTTPServer(ss.getServerSettings());
               svr.startServer();
               startServer = false;
          }
     }
     
     public static void w(Object... t)
     {
          StringBuilder sb = new StringBuilder();
          for (Object s : t)
          {
               if (s != null)
               {
                    sb.append(s.toString());
               }
          }
          System.out.println(sb.toString());
     }   
     
     /**
      * The main() method is ignored in correctly deployed JavaFX application. main() serves only as
      * fallback in case the application can not be launched through deployment artifacts, e.g., in
      * IDEs with limited FX support. NetBeans ignores main().
      *
      * @param args the command line arguments
      */
     public static void main(String[] args) {
          String opx;
          Scanner scanner = new Scanner(System.in);

          do {
               try {
                    Main m = new Main();
                    //m.doShit();
               } catch (Exception ex) {
                    ex.printStackTrace();
               }

               System.out.println("? ");
               opx = scanner.nextLine();

          } while (!"x".equalsIgnoreCase(opx));
     }
     
     /**
      * HTTP client is good right, it is good so there's nto need to worry about that ..
      * 
      * ## So it's server now, so .. how does it work?
      * - crate server
      * - pass server ahndler, that's teh part that does the sitching /server hadler does the switch/
      * - page ahndler hanldes page from strat to finish, a - z. Komprendeh!
      * 
      * yo() - handles the switch...
      * The we pass page handler .. wgich handles the page from start to finish.
      * - call in, pass in if necessary
      * request object is embedded inside the ..
      * 
      * // 1.
      * Make sure that all the data required to hanlde requst is made available .. [all]
      * // 2. Hey how about trestign every branch .. so a lot fo stuff can happen, so test all
      * - under usual circumstance(?) - we can read with no problem,header the content.
      * UNDEr UNUSUAL circumstances .. What are those? let's amek a lits then test each.
      * 
      */     
     
     /**
      * ====
      * BUGS
      * ====
      * 1. Bug, priority - not high.
      * When reading header - what if teh value is bad, eg. alphabets in length. [not handled prop.]
      * 
      */
}
