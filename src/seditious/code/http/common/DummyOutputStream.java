/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream that does nothing.
 * @author user
 */
public class DummyOutputStream extends OutputStream {

     @Override
     public void write(int b) throws IOException {
     }
}
