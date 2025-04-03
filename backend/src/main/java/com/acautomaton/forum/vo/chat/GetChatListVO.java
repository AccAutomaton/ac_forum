package com.acautomaton.forum.vo.chat;

import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetChatListVO {
    PageHelperVO<GetItemOfChatListVO> chatList;
    String participantAvatarPrefix = CosFolderPath.AVATAR.getPath();

    public GetChatListVO(PageHelperVO<GetItemOfChatListVO> chatList, Integer receiverUid) {
        List<GetItemOfChatListVO> newList = new ArrayList<>();
        for (int i = 0; i < chatList.getRecords().size(); i++) {
            GetItemOfChatListVO item = (GetItemOfChatListVO) chatList.getRecords().get(i);
            if (!item.getParticipant1Uid().equals(receiverUid)) {
                item.setNotReadMessages(item.getNotReadMessages2());
            }
            item.setNotReadMessages2(null);
            newList.add(item);
        }
        chatList.setRecords(newList);
        this.chatList = chatList;
    }
}
