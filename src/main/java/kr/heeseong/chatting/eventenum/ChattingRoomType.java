package kr.heeseong.chatting.eventenum;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum ChattingRoomType {
    MANY_TO_MANY("다대다")
    , ONE_TO_MANY("일대다")
    , APPROVAL("메시지 승인모드")
    ;

    ChattingRoomType(String value) {
    }
}
