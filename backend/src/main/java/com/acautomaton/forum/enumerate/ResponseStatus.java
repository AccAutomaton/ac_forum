package com.acautomaton.forum.enumerate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseStatus {
    SUCCESS(200, "success"),
    ERROR(403, "error"),
    JWT_ERROR(401, "jwt_error"),
    TOO_FREQUENT_ERROR(503, "too_frequent_Error");

    private final Integer code;
    private final String status;
}
