/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seditious.code.http.server;

/**
 *
 * @author user
 */
public enum CBHStat
{
     OK,
     IO_ERROR, 
     CONN_CLOSED,
     READ_LENGTH_FAILED,
     FILE_NOT_FOUND, 
     GEN_SERVER_ERROR,
     CREATE_TEMP_FILE_FAILED, 
     TEMP_DIR_NOT_SET, 
     NOT_IMPLEMENTED, 
     END_INDETERMINABLE,
     CONTENT_BODY_NOT_ALLOWED,
     TIMEOUT_READING,
     READ_CHUNK_ERROR,
     NOT_ALLOWED
}
