package kr.heeseong.chatting.old.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
public class MessageEventOld {

	private int idx;
	private int messageEventType;
	private int programIdx;
	private long fromUserIdx;
	private long toUserIdx;
	private String userId;
	private String userName;
	private String message;


	public MessageEventOld(int messageEventType, int programIdx, long toUserIdx, long fromUserIdx, String userName, String msg, String userId) {
		this.messageEventType = messageEventType;
		this.programIdx = programIdx;
		this.fromUserIdx = fromUserIdx;
		this.toUserIdx = toUserIdx;
		this.userId = userId;
		this.userName = userName;
		this.message = msg;
	}
}
