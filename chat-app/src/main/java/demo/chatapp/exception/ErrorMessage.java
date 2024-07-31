package demo.chatapp.exception;

public class ErrorMessage {
    private final String msg;
    private final String code;

    public ErrorMessage(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }
}
