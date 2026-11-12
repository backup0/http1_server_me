package seditious.code.http.hdr;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<HeaderValue> parse(String s) {
        
        final List<HeaderValue> values = new ArrayList<>();
        
        try {
            final String[] rawValues = s.split(","); // split the different

            for (String v : rawValues) {
                // quality value.
                final HeaderValue rslt;
                final String[] value = v.split(";");

                switch (value.length) {
                    case 1:
                        // no param
                        rslt = new HeaderValue(value[0].trim());
                        break;
                    case 2:
                        // with param
                        final String[] vp = value[1].split("=");
                        // let's the user handle this.
                        if (vp.length == 2) {
                            rslt = new HeaderValue(value[0].trim(), vp[0].trim(), vp[1].trim());
                        }
                        else {
                            // bad
                            rslt = new HeaderValue("");
                        }
                        break;
                    case 0:
                    default:
                        // no idea.
                        rslt = new HeaderValue("");
                        break;
                }
                values.add(rslt);
            }
        }
        catch (Exception e) {
            values.clear();
        }
        return values;
    }
}
