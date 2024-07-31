package demo.chatapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorMessage handleBadRequestError(BadRequestException e) {
        return new ErrorMessage(e.getMessage(), e.getClass().getSimpleName());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorMessage handleUnAuthError(UnauthorizedException e) {
        return new ErrorMessage(e.getMessage(), e.getClass().getSimpleName());
    }
}
