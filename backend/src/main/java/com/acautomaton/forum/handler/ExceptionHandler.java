package com.acautomaton.forum.handler;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.TooManyListenersException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = {
                ForumIllegalArgumentException.class,
                UsernameNotFoundException.class
            }
    )
    public Response Exception(Exception e) {
        return Response.error(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = TooManyListenersException.class)
    public Response SpeedException(Exception e) {
        return Response.tooFrequentError(e.getMessage());
    }
}
