package com.acautomaton.forum.vo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetItemOfChatListVO {
    Integer chatId;
    Integer participant1Uid;
    String participant1Nickname;
    String participant1Avatar;
    Integer participant2Uid;
    String participant2Nickname;
    String participant2Avatar;
    Integer notReadMessages;
    Integer notReadMessages2;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    String latestChatMessage;
}
