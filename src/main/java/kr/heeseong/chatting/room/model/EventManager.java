package kr.heeseong.chatting.room.model;


import kr.heeseong.chatting.eventenum.MessageEventType;

public class EventManager {
    public static MessageEvent makeCreateRoomEvent(ChattingRoom chattingRoom) {
        return MessageEvent.builder()
                .messageEventType(MessageEventType.CREATE_CHATROOM.getValue())
                .programIdx(chattingRoom.getChattingRoomSeq())
                .toUserIdx(0L)
                .fromUserIdx(chattingRoom.getAdminIdx())
                .userName(chattingRoom.getUserName())
                .message(chattingRoom.getDescription())
                .build();
    }

//    public static MessageEvent enterRoomEvent(ChattingRoom chattingRoom) {
//        return MessageEvent.builder()
//                .messageEventType(MessageEventType.ENTER_USER.getValue())
//                .programIdx(chattingRoom.getChattingRoomSeq())
//                .toUserIdx(0L)
//                .fromUserIdx(chattingRoom.getUserIdx())
//                .userId(chattingRoom.getUserId())
//                .userName(chattingRoom.getUserName())
//                .build();
//    }

    public static MessageEvent makeLeaveRoomEvent(Long programIdx, long userIdx) {
        return MessageEvent.builder()
                .messageEventType(MessageEventType.LEAVE_USER.getValue())
                .programIdx(programIdx)
                .fromUserIdx(userIdx)
                .build();
    }

    public static MessageEvent removeChatRoomEvent(Long roomIdx) {
        return MessageEvent.builder()
                .messageEventType(MessageEventType.REMOVE_CHATROOM.getValue())
                .programIdx(roomIdx)
                .build();
    }

    public static MessageEvent cloneEvent(MessageEvent messageEventOld) {
        return MessageEvent.builder()
                .programIdx(messageEventOld.getProgramIdx())
                .fromUserIdx(messageEventOld.getFromUserIdx())
                .messageEventType(messageEventOld.getMessageEventType())
                .userId(messageEventOld.getUserId())
                .userName(messageEventOld.getUserName())
                .toUserIdx(messageEventOld.getToUserIdx())
                .message(messageEventOld.getMessage())
                .build();
    }
}
