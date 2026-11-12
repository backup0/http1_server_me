package seditious.code.http;

import seditious.code.http.common.HTTPStatus;
import seditious.code.http.common.Headers;
import seditious.code.http.server.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class THandler implements RequestHandler {

    @Override
    public PageHandler yo(HTTPRequest rqst) {

        final PageHandler x;
        switch (rqst.getPath().get()) {
            case "/mentadak.jpg":
                x = new MentadakJpg(rqst);
                break;
            case "/lesbea.jpg":
                x = new LesbeaJpg(rqst);
                break;
            case "/dutch_beach.jpg":
                x = new DutchBeachJpg(rqst);
                break;
            case "/favicon.ico":
                x = new Icon(rqst);
                break;
            case "/dufus":
                x = new Dufus(rqst);
                break;
            default:
                x = new XY(rqst);
                break;
        }
        return x;
    }

    public class Icon extends PageHandler {
        /**
         * @param rqst HTTPRequest instance.
         */
        public Icon(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {

            try {
                File f = new File("D:\\Download\\wptest\\nn_icon.PNG");
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP200());
                h.setOption(Headers.CONTENT_LENGTH,f.length() + "");
                h.setOption(Headers.CONTENT_TYPE, "image/png");
                h.setData(new FileInputStream(f));
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class XYZ extends PageHandler {
        /**
         * @param rqst HTTPRequest instance.
         */
        public XYZ(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {

            try {
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP404());
                h.setOption(Headers.CONTENT_LENGTH,"0");
                h.setData(new ByteArrayInputStream(new byte[0]));
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class XY extends PageHandler {
        /**
         * @param rqst HTTPRequest instance.
         */
        public XY(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {

            try {
                File f = new File("D:\\etemp\\stupid_shit.html");
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP200());
                h.setOption(Headers.CONTENT_LENGTH,f.length() + "");
                h.setOption(Headers.CONTENT_TYPE, "text/html");
                h.setData(new FileInputStream(f)); // will be closed by caller.
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class MentadakJpg extends PageHandler {

        /**
         * @param rqst HTTPRequest instance.
         */
        public MentadakJpg(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {
            try {
                File f = new File("D:\\Download\\delete_naw\\bigbox_renamer_2\\mentadak.jpeg");
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP200());
                h.setOption(Headers.CONTENT_LENGTH,f.length() + "");
                h.setOption(Headers.CONTENT_TYPE, "image/jpeg");
                h.setData(new FileInputStream(f));
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class LesbeaJpg extends PageHandler {

        /**
         * @param rqst HTTPRequest instance.
         */
        public LesbeaJpg(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {
            try {
                File f = new File("D:\\Download\\delete_naw\\bigbox_renamer_2\\lesbea-78996.jpg");
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP200());
                h.setOption(Headers.CONTENT_LENGTH,f.length() + "");
                h.setOption(Headers.CONTENT_TYPE, "image/jpeg");
                h.setData(new FileInputStream(f));
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class DutchBeachJpg extends PageHandler {

        /**
         * @param rqst HTTPRequest instance.
         */
        public DutchBeachJpg(HTTPRequest rqst) {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData) {
            try {
                File f = new File("D:\\Download\\delete_naw\\bigbox_renamer_2\\Buxom At The Beach.jpg");
                HTTPOut h = new HTTPOut();
                h.setStatus(new HTTPStatus.HTTP200());
                h.setOption(Headers.CONTENT_LENGTH,f.length() + "");
                h.setOption(Headers.CONTENT_TYPE, "image/jpeg");
                h.setData(new FileInputStream(f));
                return h;
            }
            catch (Exception x){
                return new HTTPOut();
            }
        }
    }

    public class Dufus extends PageHandler
    {

        /**
         * @param rqst HTTPRequest instance.
         */
        public Dufus(HTTPRequest rqst)
        {
            super(rqst);
        }

        @Override
        public HTTPOut out(ExtraData exData)
        {
            final var s = new StringBuilder();
            s.append(request.getPath()).append("\n");
            s.append(System.currentTimeMillis()).append("\n\n");
            s.append(Thread.currentThread().getName()).append("\n");
            s.append("Correct - but not super correcert").append("\n");
            s.append("Going to take a little bit OF TYPING ..");

            ByteArrayInputStream bis = new ByteArrayInputStream(s.toString().getBytes());

            HTTPOut h = new HTTPOut();
            h.setStatus(new HTTPStatus.HTTP200());
            h.setOption(Headers.CONTENT_LENGTH,bis.available() + "");
            h.setOption(Headers.CONTENT_TYPE, "text/plain");
            h.setData(bis);
            return h;
        }
    }

}
