package seditious.code.http.server;

import seditious.code.http.common.Headers;
import seditious.code.http.common.ListHelper;
import seditious.code.http.common.StringPair;
import seditious.code.http.hdr.HeaderValue;
import seditious.code.http.hdr.Parser;

import java.nio.file.Path;
import java.util.List;

/**
 * Handle content body read and write ... Contents have to be read and written. We may or may not
 * have the request. Header is ok, request is not.
 *
 * @author user
 */
public class ContentBodyHandler
{
    public enum Stat {
        OK,
        NO_CONTENT,
        BAD_REQUEST,
        SERVER_ERROR
    }

    private enum TerminationMethod {
        LENGTH,
        CHUNK,
        NONE,
        ERROR
    }

     private final String identity = "identity";
     private String error;

     private final Stat stat;
     private final ContentBodyProx wts;

     public ContentBodyHandler (ServerSettings ss, PageHandler ph) {

         final HTTPRequest rqst = ph.request;
         final TerminationMethod termMeth = determineTerminationMethod(rqst);
         final boolean contentAllowed = isContentBodyAllowed(rqst);

         ContentBodyProx wts0 = null;
         Stat stat0;
         String error0 = "";

         try {

             if (contentAllowed) {

                 final Path tempDir = ss.getTempDir();

                 switch (termMeth) {
                     case LENGTH:
                         wts0 = new ContentBodyProx(tempDir, rqst.getHeaders(), ph, true);
                         stat0 = Stat.OK;
                         break;
                     case CHUNK:
                         wts0 = new ContentBodyProx(tempDir, rqst.getHeaders(), ph, false);
                         stat0 = Stat.OK;
                         break;
                     case ERROR:
                         stat0 = Stat.BAD_REQUEST;
                         // todo: more useful message
                         error0 = "wtf?";
                         break;
                     case NONE:
                         stat0 = Stat.BAD_REQUEST;
                         error0 = "No length or encoding.";
                         break;
                     default:
                         stat0 = Stat.SERVER_ERROR;
                         break;
                 }
             }
             else {

                 if (termMeth == TerminationMethod.NONE) {
                     stat0 = Stat.NO_CONTENT;
                 }
                 else {
                     stat0 = Stat.BAD_REQUEST;
                 }
             }
         }
         catch (Exception e) {
             stat0 = Stat.SERVER_ERROR;
             error0 = "Some server error.";
             wts0 = null;
         }

         stat = stat0;
         error = error0;
         wts = wts0;
     }

     private TerminationMethod determineTerminationMethod(HTTPRequest rqst) {

         final List<StringPair> hdr = rqst.getHeaders();
         final String[] length = ListHelper.getValues(Headers.CONTENT_LENGTH, hdr);
         final String[] tranEnc = ListHelper.getValues(Headers.TRANSFER_ENCODING, hdr);

         final TerminationMethod rslt;

         if (length.length == 0) {
             rslt = noLength(tranEnc);
         }
         else if (length.length == 1) {
             rslt = yesLength(tranEnc);
         }
         else {
             // more than one length - which one do I use?
             rslt = TerminationMethod.ERROR;
         }
         return rslt;
     }

     private TerminationMethod noLength(String[] tranEnc) {
         // no length
         // we need chunked here

         if (tranEnc.length == 0) {
             return TerminationMethod.NONE;
         }
         else if (tranEnc.length > 1) {
             return TerminationMethod.ERROR;
         }

         final TerminationMethod rslt;
         final List<HeaderValue> hv = Parser.parse(tranEnc[0]);
         final String chunked = "chunked";

         if (hv.size() == 1) {
             // we expect chunked here
             final HeaderValue v = hv.get(0);
             if (chunked.equalsIgnoreCase(v.getValue())) {
                 //ok
                 rslt = TerminationMethod.CHUNK;
             }
             else {
                 // not ok ...
                 // identity alone isn't enough.
                 rslt = TerminationMethod.ERROR;
             }
         }
         else if (hv.size() == 2) {
             // we expect chunked + identity here
             final String t0 = hv.get(0).getValue() + hv.get(1).getValue();
             final String[] tb = new String[]{chunked + identity, identity + chunked};

             if (t0.equalsIgnoreCase(tb[0]) || t0.equalsIgnoreCase(tb[1])) {
                 rslt = TerminationMethod.CHUNK;
             }
             else {
                 rslt = TerminationMethod.ERROR;
             }
         }
         else {
             // bad
             rslt = TerminationMethod.ERROR;
         }
         return rslt;
     }

     private TerminationMethod yesLength(String[] tranEnc) {

         // has length. OK?
         final TerminationMethod rslt;

         switch (tranEnc.length) {
             case 0:
                 // no transfer encoding
                 rslt = TerminationMethod.LENGTH;
                 break;
             case 1:
                 final List<HeaderValue> hv = Parser.parse(tranEnc[0]);
                 if (hv.size() == 1) {
                     final HeaderValue v = hv.get(0);
                     if (identity.equalsIgnoreCase(v.getValue())) {
                         // identity is ok
                         rslt = TerminationMethod.LENGTH;
                     }
                     else {
                         // not ok.
                         rslt = TerminationMethod.ERROR;
                     }
                 }
                 else {
                     // can't have more than 1.
                     rslt = TerminationMethod.ERROR;
                 }
                 break;
             default:
                 // more than 1
                 rslt = TerminationMethod.ERROR;
                 break;
         }
         return rslt;
     }

    private boolean isContentBodyAllowed(HTTPRequest rqst) {

        switch (rqst.getMethod())
        {
            // these can't have content body ...
            case GET:
            case CONNECT:
            case HEAD:
            case OPTIONS:
            case TRACE:
                // these methods don't accept content body
                return false;
            default: // stuff that can have content body.
                return true;
        }
    }

     public Stat getStat() {
         return stat;
     }

     public ContentBodyProx getContentBodyProx() {
        return wts;
     }

     public String getErrorMsg() {
         return error;
     }
}
