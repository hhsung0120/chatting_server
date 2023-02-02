package kr.heeseong.chatting.message.model;

import kr.heeseong.chatting.eventenum.MessageEventType;
import kr.heeseong.chatting.room.model.ChattingRoom;
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
        this.fromUserIdx = chattingRoom.getUserIdx();
        this.userId = chattingRoom.getUserId();
        this.userName = chattingRoom.getUserName();
        this.chattingRoomSeq = chattingRoom.getChattingRoomSeq();
    }

    public static MessageEvent setMessageCloneEvent(MessageEvent waitApprovalMessage, MessageEventType eventType) {
        return MessageEvent.waitApprovalMessageEventBuilder()
                .waitApprovalMessage(waitApprovalMessage)
                .eventType(eventType)
                .build();
    }

    @Builder(builderClassName = "waitApprovalMessageEventBuilder", builderMethodName = "waitApprovalMessageEventBuilder")
    public MessageEvent(MessageEvent waitApprovalMessage, MessageEventType eventType) {
        this.fromUserIdx = waitApprovalMessage.getFromUserIdx();
        this.userId = waitApprovalMessage.getUserId();
        this.userName = waitApprovalMessage.getUserName();
        this.toUserIdx = waitApprovalMessage.getToUserIdx();
        this.message = waitApprovalMessage.getMessage();
        this.eventType = eventType;
    }
}
