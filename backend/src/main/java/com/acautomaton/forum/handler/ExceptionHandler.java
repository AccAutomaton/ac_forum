package com.acautomaton.forum.handler;

import com.acautomaton.forum.exception.*;
import com.acautomaton.forum.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = {
                ForumIllegalArgumentException.class,
                UsernameNotFoundException.class,
                ForumVerifyException.class,
                ForumException.class,
                ForumObjectExpireException.class,
                ForumExistentialityException.class
            }
    )
    public Response Exception(Exception e) {
        return Response.error(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response ArgumentValidateException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return Response.error(message);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ForumRequestTooFrequentException.class)
    public Response SpeedException(Exception e) {
        return Response.tooFrequentError(e.getMessage());
    }
}
