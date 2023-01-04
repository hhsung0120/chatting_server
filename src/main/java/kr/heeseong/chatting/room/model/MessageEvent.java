package kr.heeseong.chatting.room.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MessageEvent {

    @Setter
    private Long idx;

    @Setter
    private int messageEventType;
    private Long programIdx;
    private Long fromUserIdx;
    private Long toUserIdx;
    private String userId;
    private String userName;
    private String message;

    public MessageEvent() {
    }

    @Builder
    public MessageEvent(Long idx, int messageEventType, Long programIdx, Long fromUserIdx, Long toUserIdx, String userId, String userName, String message) {
        this.idx = idx;
        this.messageEventType = messageEventType;
        this.programIdx = programIdx;
        this.fromUserIdx = fromUserIdx;
        this.toUserIdx = toUserIdx;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
    }

    public MessageEvent(int messageEventType, Long programIdx, Long toUserIdx, Long fromUserIdx, String userName, String msg, String userId) {
        this.messageEventType = messageEventType;
        this.programIdx = programIdx;
        this.fromUserIdx = fromUserIdx;
        this.toUserIdx = toUserIdx;
        this.userId = userId;
        this.userName = userName;
        this.message = msg;
    }
}
