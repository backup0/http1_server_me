package seditious.code.http.hdr;

public class HeaderValue {

    private final String _value;

    private boolean _hasParam;
    private final String _paramName;
    private final String _paramValue;


    public HeaderValue(String value) {
        this(value, "", "", false);
    }

    public HeaderValue(String value, String paramName, String paramValue) {
        this(value, "", "", true);
    }

    private HeaderValue(String value, String paramName, String paramValue, boolean hasParam) {
        _value = value;
        _paramName = paramName;
        _paramValue = paramValue;
        _hasParam = hasParam;
    }

    public String getValue() {
        return _value;
    }

    public String getParamName() {
        return _paramName;
    }

    public String getParamValue() {
        return _paramValue;
    }

    public boolean hasParam() {
        return _hasParam;
    }
}
