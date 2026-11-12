package seditious.code.http.exception;

public class BadRequestException extends Exception {
    public BadRequestException() {
        this("Bad request");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
