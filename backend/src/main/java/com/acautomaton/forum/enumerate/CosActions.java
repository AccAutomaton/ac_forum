package com.acautomaton.forum.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CosActions {
    PUT_OBJECT(new String[]{"cos:PutObject"}),
    GET_OBJECT(new String[]{"cos:GetObject"}),
    OBJECT_EXSIST(new String[]{"cos:HeadObject"});

    private final String[] actions;
}
