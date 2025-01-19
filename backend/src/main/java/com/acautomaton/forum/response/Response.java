package com.acautomaton.forum.response;

import com.acautomaton.forum.enumerate.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class Response extends HashMap<String, Object> {
    public static Response success() {
        return Response.of(ResponseStatus.SUCCESS, null, Map.of("result", "success"));
    }

    public static Response success(Object data) {
        return Response.of(ResponseStatus.SUCCESS, null, data);
    }

    public static Response error(String message) {
        return Response.of(ResponseStatus.ERROR, message, null);
    }

    public static Response jwtError(String message) {
        return Response.of(ResponseStatus.JWT_ERROR, message, null);
    }

    public static Response tooFrequentError(String message) {
        return Response.of(ResponseStatus.TOO_FREQUENT_ERROR, message, null);
    }

    public static Response of(ResponseStatus status, String message, Object data) {
        Response response = new Response();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public Response put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}