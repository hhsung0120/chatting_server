package kr.heeseong.chatting.old.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Data
public class ChattingUserDataOld {

	private ChattingUsersOld chattingUsersOld;
	private int programIdx;
	private ArrayBlockingQueue<MessageEventOld> messageQueue;
	private long latestMessageTime;
	private long DEFAULT_MESSAGE_TIMEOUT = 60 * 1000 * 2; // 2 minutes
	private long userTimeout = DEFAULT_MESSAGE_TIMEOUT;

	public ChattingUserDataOld(ChattingUsersOld chattingUsersOld) {
		this.chattingUsersOld = chattingUsersOld;
		messageQueue = new ArrayBlockingQueue<>(10);
		latestMessageTime = System.currentTimeMillis();
	}

	public long getUserIdx() {
		return chattingUsersOld.getUserIdx();
	}

	public long getInternalIdx() {
		return chattingUsersOld.getInternalIdx();
	}

	public String getUserId() {
		return chattingUsersOld.getUserId();
	}

	public String getUserName() {
		return chattingUsersOld.getUserName();
	}

	public boolean isAdmin() {
		return chattingUsersOld.isAdmin();
	}

	public void postMessage(MessageEventOld messageEventOld) {
		if (messageQueue != null) {
			try {
				messageQueue.add(messageEventOld);
			} catch (Exception e) {
				decreaseUserTimeOut();
				e.printStackTrace();
			}
		}
	}
	
	private void decreaseUserTimeOut() {
		userTimeout = userTimeout / 2;
		if (userTimeout < 60000) {
			userTimeout = 60000;
		}
	}
	
	public boolean checkTimeout() {
		if (latestMessageTime != 0) {
			if ((System.currentTimeMillis() - latestMessageTime) > userTimeout) {
				return true;
			}
		}
		return false;
	}
	
	private void setLatestTime() {
		latestMessageTime = System.currentTimeMillis();
	}
	
	@JsonIgnore
	public ArrayList<MessageEventOld> getEvents() {
		setLatestTime();
		ArrayList<MessageEventOld> messageEventOlds = new ArrayList<>();
		if (messageQueue != null) {
			try {
				MessageEventOld messageEventOld = messageQueue.poll(5000, TimeUnit.MILLISECONDS);
				if (messageEventOld != null && messageQueue != null) {
					messageEventOlds.add(messageEventOld);
					if (messageQueue.size() != 0) {
						for (int i = 0; i < messageQueue.size(); i++) {
							messageEventOlds.add(messageQueue.take());
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return messageEventOlds;
	}
	
	public void removeAll() {
		if (messageQueue != null) {
			messageQueue.clear();
			messageQueue = null;
		}
	}



}
