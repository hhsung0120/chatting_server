package kr.heeseong.chatting.room.model;

import kr.heeseong.chatting.eventenum.MessageEventType;
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
    private MessageEventType eventType;
    private Long fromUserIdx;
    private Long toUserIdx;
    private String userId;
    private String userName;
    private String message;
    private Long chattingRoomSeq;

    public MessageEvent() {
    }

    @Builder
    public MessageEvent(Long idx, int messageEventType, Long programIdx, Long fromUserIdx, Long toUserIdx, String userId, String userName, String message) {
        this.idx = idx;
        this.fromUserIdx = fromUserIdx;
        this.toUserIdx = toUserIdx;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
    }

    @Builder(builderClassName = "createRoomBuilder", builderMethodName = "createRoomBuilder")
    public MessageEvent(ChattingRoom chattingRoom) {
        this.eventType = MessageEventType.ENTER_USER;
        this.toUserIdx = 0L;
        this.fromUserIdx = chattingRoom.getUserIdx();
        this.userId = chattingRoom.getUserId();
        this.userName = chattingRoom.getUserName();
        this.chattingRoomSeq = chattingRoom.getChattingRoomSeq();
    }
}
