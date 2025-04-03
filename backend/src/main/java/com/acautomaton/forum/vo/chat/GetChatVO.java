package com.acautomaton.forum.vo.chat;

import com.acautomaton.forum.enumerate.CosFolderPath;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetChatVO {
    Integer chatId;
    Integer receiverUid;
    String receiverNickname;
    String receiverAvatar;
    Integer notReadMessages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    String latestChatMessage;

    public GetChatVO(Integer chatId, Integer receiverUid, String receiverNickname, String receiverAvatar, Integer notReadMessages, Date updateTime, String latestChatMessage) {
        this.chatId = chatId;
        this.receiverUid = receiverUid;
        this.receiverNickname = receiverNickname;
        this.receiverAvatar = CosFolderPath.AVATAR + receiverAvatar;
        this.notReadMessages = notReadMessages;
        this.updateTime = updateTime;
        this.latestChatMessage = latestChatMessage;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = CosFolderPath.AVATAR + receiverAvatar;
    }
}
