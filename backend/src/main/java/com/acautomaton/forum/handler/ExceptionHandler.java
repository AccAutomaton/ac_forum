package com.acautomaton.forum.handler;

import com.acautomaton.forum.exception.*;
import com.acautomaton.forum.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            ForumIllegalArgumentException.class,
            UsernameNotFoundException.class,
            ForumVerifyException.class,
            ForumException.class,
            ForumObjectExpireException.class,
            ForumExistentialityException.class,
            ForumIllegalAccountException.class
    })
    public Response Exception(Exception e) {
        return Response.error(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response ArgumentValidateException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        Stream<String> errorStream = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage);
        String message = errorStream.toList().getLast();
        return Response.error(message);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ConstraintViolationException.class)
    public Response ConstraintViolationException(ConstraintViolationException e) {
        return Response.error(e.getMessage().substring(e.getMessage().indexOf(":") + 2));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ForumRequestTooFrequentException.class)
    public Response SpeedException(Exception e) {
        return Response.tooFrequentError(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ForumEmailException.class)
    public Response EmailServiceException(Exception e) {
        log.warn("邮件服务异常: {}", e.getMessage());
        return Response.error("服务器错误，请稍后再试");
    }
}
