package kr.heeseong.chatting.old.model;


import kr.heeseong.chatting.old.event_enum.MessageEventType;

public class EventManagerOld {
	public static MessageEventOld makeCreateRoomEvent(ChattingRoomOld chattingRoom) {
		MessageEventOld messageEventOld = new MessageEventOld();
		messageEventOld.setMessageEventType(MessageEventType.CREATE_CHATROOM.getValue());
		messageEventOld.setProgramIdx(chattingRoom.getProgramIdx());
		messageEventOld.setToUserIdx(0);
		messageEventOld.setFromUserIdx(chattingRoom.getAdminIdx());
		messageEventOld.setUserName(chattingRoom.getName());
		messageEventOld.setMessage(chattingRoom.getDescription());
		
		return messageEventOld;
	}

	public static MessageEventOld makeEnterRoomEvent(int roomIdx, ChattingUsersOld chatroomUser) {
		MessageEventOld messageEventOld = new MessageEventOld();
		messageEventOld.setMessageEventType(MessageEventType.ENTER_USER.getValue());
		messageEventOld.setProgramIdx(roomIdx);
		messageEventOld.setToUserIdx(0);
		messageEventOld.setFromUserIdx(chatroomUser.getUserIdx());
		messageEventOld.setUserId(chatroomUser.getUserId());
		messageEventOld.setUserName(chatroomUser.getUserName());
		
		return messageEventOld;
	}
	
	public static MessageEventOld makeLeaveRoomEvent(int programIdx, long userIdx) {
		MessageEventOld messageEventOld = new MessageEventOld();
		messageEventOld.setMessageEventType(MessageEventType.LEAVE_USER.getValue());
		messageEventOld.setProgramIdx(programIdx);
		messageEventOld.setFromUserIdx(userIdx);
		
		return messageEventOld;
	}
	
	public static MessageEventOld removeChatRoomEvent(int roomIdx) {
		MessageEventOld messageEventOld = new MessageEventOld();
		messageEventOld.setMessageEventType(MessageEventType.REMOVE_CHATROOM.getValue());
		messageEventOld.setProgramIdx(roomIdx);
		
		return messageEventOld;
	}
	
	public static MessageEventOld cloneEvent(MessageEventOld messageEventOld) {
		MessageEventOld newMessageEventOld = new MessageEventOld();
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
