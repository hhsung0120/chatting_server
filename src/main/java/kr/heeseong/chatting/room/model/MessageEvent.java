package kr.heeseong.chatting.room.model;

import kr.heeseong.chatting.old.event_enum.MessageEventType;
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

    public MessageEvent(ChattingRoom chattingRoom) {
        this.messageEventType = MessageEventType.ENTER_USER.getValue();
        this.programIdx = chattingRoom.getChattingRoomSeq();
        this.toUserIdx = 0L;
        this.fromUserIdx = chattingRoom.getUserIdx();
        this.userId = chattingRoom.getUserId();
        this.userName = chattingRoom.getUserName();
    }
}
