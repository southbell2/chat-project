package demo.chatapp.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message)  {
        super(message);
    }
}
