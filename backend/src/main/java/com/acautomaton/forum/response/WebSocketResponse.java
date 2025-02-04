package com.acautomaton.forum.response;

import com.acautomaton.forum.enumerate.WebSocketResponseType;

import java.util.HashMap;

public class WebSocketResponse extends HashMap<String, Object> {
    public static WebSocketResponse error(String message) {
        return WebSocketResponse.of(WebSocketResponseType.ERROR, message);
    }

    public static WebSocketResponse handshake(Object data) {
        return WebSocketResponse.of(WebSocketResponseType.HANDSHAKE, data);
    }

    public static WebSocketResponse message(Object data) {
        return WebSocketResponse.of(WebSocketResponseType.MESSAGE, data);
    }

    public static WebSocketResponse heartbeat(Object data) {
        return WebSocketResponse.of(WebSocketResponseType.HEARTBEAT, data);
    }

    public static WebSocketResponse of(WebSocketResponseType type, Object data) {
        WebSocketResponse response = new WebSocketResponse();
        response.put("type", type);
        response.put("data", data);
        return response;
    }

    public WebSocketResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
