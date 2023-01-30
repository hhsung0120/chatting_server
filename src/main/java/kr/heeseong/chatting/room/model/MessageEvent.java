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

    @Builder(builderClassName = "createRoomBuilder", builderMethodName = "createRoomBuilder")
    public MessageEvent(ChattingRoom chattingRoom) {
        this.eventType = MessageEventType.ENTER_USER;
        this.toUserIdx = 0L;
        this.fromUserIdx = chattingRoom.getUserIdx();
        this.userId = chattingRoom.getUserId();
        this.userName = chattingRoom.getUserName();
        this.chattingRoomSeq = chattingRoom.getChattingRoomSeq();
    }

    @Builder(builderClassName = "waitApprovalMessageEventBuilder", builderMethodName = "waitApprovalMessageEventBuilder")
    public MessageEvent(MessageEvent waitApprovalMessage) {
        this.fromUserIdx = waitApprovalMessage.getFromUserIdx();
        this.userId = waitApprovalMessage.getUserId();
        this.userName = waitApprovalMessage.getUserName();
        this.toUserIdx = waitApprovalMessage.getToUserIdx();
        this.message = waitApprovalMessage.getMessage();
        this.eventType = MessageEventType.WAIT_APPROVAL_MSG;
    }
}
