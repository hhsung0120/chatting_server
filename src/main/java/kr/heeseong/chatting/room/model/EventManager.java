package kr.heeseong.chatting.room.model;


import kr.heeseong.chatting.old.event_enum.MessageEventType;
import kr.heeseong.chatting.user.model.ChattingUser;

public class EventManager {
	public static MessageEvent makeCreateRoomEvent(ChattingRoom chattingRoom) {
		MessageEvent messageEventOld = new MessageEvent();
		messageEventOld.setMessageEventType(MessageEventType.CREATE_CHATROOM.getValue());
		messageEventOld.setProgramIdx(chattingRoom.getChattingRoomSeq());
		messageEventOld.setToUserIdx(0);
		messageEventOld.setFromUserIdx(chattingRoom.getAdminIdx());
		messageEventOld.setUserName(chattingRoom.getName());
		messageEventOld.setMessage(chattingRoom.getDescription());
		
		return messageEventOld;
	}

	public static MessageEvent makeEnterRoomEvent(Long roomIdx, ChattingUser chatroomUser) {
		MessageEvent messageEventOld = new MessageEvent();
		messageEventOld.setMessageEventType(MessageEventType.ENTER_USER.getValue());
		messageEventOld.setProgramIdx(roomIdx);
		messageEventOld.setToUserIdx(0);
		messageEventOld.setFromUserIdx(chatroomUser.getUserIdx());
		messageEventOld.setUserId(chatroomUser.getUserId());
		messageEventOld.setUserName(chatroomUser.getUserName());
		
		return messageEventOld;
	}
	
	public static MessageEvent makeLeaveRoomEvent(Long programIdx, long userIdx) {
		MessageEvent messageEventOld = new MessageEvent();
		messageEventOld.setMessageEventType(MessageEventType.LEAVE_USER.getValue());
		messageEventOld.setProgramIdx(programIdx);
		messageEventOld.setFromUserIdx(userIdx);
		
		return messageEventOld;
	}
	
	public static MessageEvent removeChatRoomEvent(Long roomIdx) {
		MessageEvent messageEventOld = new MessageEvent();
		messageEventOld.setMessageEventType(MessageEventType.REMOVE_CHATROOM.getValue());
		messageEventOld.setProgramIdx(roomIdx);
		
		return messageEventOld;
	}
	
	public static MessageEvent cloneEvent(MessageEvent messageEventOld) {
		MessageEvent newMessageEventOld = new MessageEvent();
		newMessageEventOld.setProgramIdx(messageEventOld.getProgramIdx());
		newMessageEventOld.setFromUserIdx(messageEventOld.getFromUserIdx());
		newMessageEventOld.setMessageEventType(messageEventOld.getMessageEventType());
		if (messageEventOld.getUserId() != null) {
			newMessageEventOld.setUserId(messageEventOld.getUserId());
		}
		if (messageEventOld.getUserName() != null) {
			newMessageEventOld.setUserName(messageEventOld.getUserName());
		}
		newMessageEventOld.setToUserIdx(messageEventOld.getToUserIdx());
		if (messageEventOld.getMessage() != null) {
			newMessageEventOld.setMessage(messageEventOld.getMessage());
		}

		return newMessageEventOld;
	}
}
