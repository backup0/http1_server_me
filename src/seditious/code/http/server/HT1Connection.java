package seditious.code.http.server;

import seditious.code.http.Main;
import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.Headers;
import seditious.code.http.common.ListHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class HT1Connection { //remove unnecessary exception like temp not created etc,

    // todo: change generic error to less generic error ..
    public enum Stat {
        READ_HEADER,
        PROCESS_RESPONSE,
        WRITE_OUT,
        CLOSE
    }

    private enum Ops {
        NONE,
        READ_HEADER,
        READ_BODY,
        GET_RESPONSE,
        WRITE
    }

    private final Socket socket;
    private final ServerSettings serverSettings;
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;
    private final byte[] buffer;
    private final int outBufferSize;
    private long lastActionTime;

    private HeaderProx hdrProx = null;
    private HTTPRequest request = null;
    private ContentBodyProx cbProx = null;
    private PageHandler pageHandler = null;
    private OutputWriter response = null;
    private byte[] remainingInput = null;
    private Ops currentOp = Ops.NONE;
    private boolean prevIsActionable = true;
    private boolean keepAlive = true;

    private Stat doNext = Stat.READ_HEADER;

    public HT1Connection(ServerSettings ss, Socket skt) throws IOException {

        serverSettings = ss;
        inputStream =  new BufferedInputStream(skt.getInputStream());
        outputStream = new BufferedOutputStream(skt.getOutputStream());
        socket = skt;
        lastActionTime = System.currentTimeMillis();
        buffer = new byte[skt.getReceiveBufferSize()];
        outBufferSize = skt.getSendBufferSize();
        socket.setSoTimeout(1);
    }

    public boolean doSomething() {

        if (doNext == Stat.CLOSE) {
            return false;
        }

        try {
            switch (currentOp) {
                case NONE:
                    hdrProx = new HeaderProx();
                    currentOp = Ops.READ_HEADER;
                    readHeader();
                    break;
                case READ_HEADER:
                    readHeader();
                    break;
                case READ_BODY:
                    readContentBody();
                    break;
                case GET_RESPONSE:
                    doTheResponseThingy();
                    break;
                case WRITE:
                    write();
                    break;
                default:
                    errorResponse(new HTTPStatus.HTTP500());
                    break;
            }

            if (prevIsActionable) {
                lastActionTime = System.currentTimeMillis();
            }
            else {
                final long connTimeout = serverSettings.getConnectionTimeout();
                if (System.currentTimeMillis() - lastActionTime > connTimeout) {
                    closeSocket();
                    doNext = Stat.CLOSE;
                }
            }
        }
        catch (Exception e) {
            closeSocket();
            doNext = Stat.CLOSE;
        }
        return prevIsActionable;
    }
    
    private void readHeader() throws Exception
    {

        final byte[] d = readBytes();

        if (d.length == 0) {
            return;
        }

        hdrProx.update(d);
        final HeaderProx.Stat stat = hdrProx.getStat();
        
        switch (stat) {
            case NOT_FOUND_YET:
                // do next is still cont here.
                // continue
                break;
            case OK:
                // todo: ok?
                request = hdrProx.getRequest();
                doExpectation();
                preReadContentBody();
                // do next, depends if no content body it's process else still cont.
                remainingInput = hdrProx.getRemainder();
                break;
            case BAD_HEADER:
            case BAD_REQUEST: 
                errorResponse(new HTTPStatus.HTTP400());
                break;
            case SERVER_ERROR: 
            default:
                errorResponse(new HTTPStatus.HTTP500());
                break;
        }
        prevIsActionable = true;
    }

    private void doExpectation() throws Exception {
        // todo: EXPECTATION
        // if expectation header included.
        // Expect: 100-continue
        // anting else bad request ...
        // then send data out, its just one line - what's teh big deal here?

        final String[] hdrs = ListHelper.getValues(Headers.EXPECT, request.getHeaders());
        if (hdrs.length > 0) {
            if ("100-continue".equalsIgnoreCase(hdrs[0])) {
                final byte[] b = "HTTP/1.1 100 Continue\r\n\r\n".getBytes(StandardCharsets.US_ASCII); //
                try {
                    outputStream.write(b);
                    outputStream.flush();
                }
                catch (Exception e) {
                    throw new Exception(e);
                }
            }
            else {
                errorResponse(new HTTPStatus.HTTP400());
            }
        }
    }
    
    private void preReadContentBody() {
        // do next is CONT
        // header processor creates a request for us.
        request = hdrProx.getRequest();
        pageHandler = serverSettings.getRequestHandler().yo(request);

        final ContentBodyHandler cbh = new ContentBodyHandler(serverSettings, pageHandler);
        switch (cbh.getStat()) {
            case OK:
                cbProx = cbh.getContentBodyProx();
                currentOp = Ops.READ_BODY;
                break;
            case NO_CONTENT:
                // now do next is overridden.
                doNext = Stat.PROCESS_RESPONSE;
                currentOp = Ops.GET_RESPONSE;
                break;
            case BAD_REQUEST:
                errorResponse(new HTTPStatus.HTTP400());
                break;
            case SERVER_ERROR:
            default:
                errorResponse(new HTTPStatus.HTTP500());
                break;
        }
    }
    
    private void readContentBody() {
        // do next is cont
        final byte[] d = readBytes();

        if (d.length == 0) {
            return;
        }

        cbProx.write(d);
        switch (cbProx.getStat()) {
            case NOT_DONE_YET:
                // doNext will be the same as before
                break;
            case OK:
                remainingInput = cbProx.getRemainder();
                doNext = Stat.PROCESS_RESPONSE;
                currentOp = Ops.GET_RESPONSE;
                break;
            case ERROR:
            default:
                errorResponse(new HTTPStatus.HTTP500());
                // doNext will be the same as before
                break;
        }
    }
    
    private byte[] readBytes() {

        // read remaining data first.
        final byte[] d;
        if (remainingInput != null) {
            d = remainingInput;
            remainingInput = null;
            prevIsActionable = true;
            return d;
        }

        // else get other stuff.
        try {
            // FIXME one time or each time:
            // - nope? Maybe not supported here.
            // socket.setSoTimeout(1); <<--- can we just call this once?

            final int i = inputStream.read(buffer);
            final byte[] b;

            if (i == 0) {
                b = new byte[0];
                prevIsActionable = false;
            }
            else if (i > 0) {
                b = Arrays.copyOfRange(buffer, 0, i);
                prevIsActionable = true;
            }
            else {
                b = new byte[0];
                prevIsActionable = false;
            }
            return b;
        }
        catch (Exception e) {
            prevIsActionable = false;
            return new byte[0];
        }
    }

    public void closeSocket() {
        try {
            socket.close();
        }
        catch (Exception ignored) {}
    }

    private void errorResponse(HTTPStatus stat) {
        // can't close ...
        // need to write out error response - so write.
        currentOp = Ops.WRITE;
        // error - so of course we're closing.
        keepAlive = false;

        final HTTPOut out = ErrorResponse.getResponse(stat, null,"No details.");
        response = new OutputWriter(out, outputStream, true, outBufferSize);
    }

    private void write() {

        final int x = response.send();
        if (x == 0) {

            if (keepAlive) {
                // reset - //todo: necessary? Probably not
                hdrProx = null;
                request = null;
                cbProx = null;
                pageHandler = null;
                response = null;
                // start with no action
                currentOp = Ops.NONE;
                doNext = Stat.READ_HEADER;
            }
            else {
                closeSocket();
                doNext = Stat.CLOSE;
            }
        }
        else if (x == 2) {
            closeSocket();
            doNext = Stat.CLOSE;
        }
    }

    private boolean doKeepAlive() {
        // todo: correct or not?
        // todo: Korrext or nawtz ..
        final String[] he = ListHelper.getValues(Headers.CONNECTION, request.getHeaders());

        if (he.length > 0) {
            return he[0].equalsIgnoreCase("Keep-Alive");
        }
        else {
            return true;
        }
    }

    private void doTheResponseThingy()
    {

        // this is called on processing thread.
        try {
            keepAlive = doKeepAlive();
            final ExtraData ed;
            if (cbProx == null) {
                ed = new ExtraData(Collections.emptyMap(), Collections.emptyList());
            }
            else {
                ed = new ExtraData(cbProx.getPOSTData(), cbProx.getFiles());
            }
            response = new OutputWriter(pageHandler.out(ed), outputStream, !keepAlive, outBufferSize);
            prevIsActionable = true;
            currentOp = Ops.WRITE;
            // back to cont - because ?, because we need to tell http server to call do something again.
            doNext = Stat.WRITE_OUT;
        }
        catch (Exception e) {
            errorResponse(new HTTPStatus.HTTP500());
        }
    }

    public Stat getWhatToDoNext() {
        return doNext;
    }
}
