package kr.heeseong.chatting.user.model;

import kr.heeseong.chatting.room.model.MessageEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@ToString
public class ChattingUserData {

    private ChattingUser chattingUser;

    private ArrayBlockingQueue<MessageEvent> messageQueue;
    private Long latestMessageTime;
    private Long DEFAULT_MESSAGE_TIMEOUT = 60 * 1000 * 2L; // 2 minutes
    private Long userTimeout = DEFAULT_MESSAGE_TIMEOUT;

    public ChattingUserData(ChattingUser chattingUser) {
        this.chattingUser = chattingUser;
        messageQueue = new ArrayBlockingQueue<>(10);
        latestMessageTime = System.currentTimeMillis();
    }

    public Long getUserIdx() {
        return chattingUser.getUserIdx();
    }

    public String getUserId() {
        return chattingUser.getUserId();
    }

    public String getUserName() {
        return chattingUser.getUserName();
    }

    public boolean isAdmin() {
        return chattingUser.isAdmin();
    }

    //메시지 전송
    public void postMessage(MessageEvent messageEvent) {
        if (messageQueue != null) {
            try {
                messageQueue.add(messageEvent);
            } catch (Exception e) {
                decreaseUserTimeOut();
                log.error("postMessage exception : {}", e.getMessage());
            }
        }
    }

    private void decreaseUserTimeOut() {
        userTimeout = userTimeout / 2;
        if (userTimeout < 60000) {
            userTimeout = 60000L;
        }
    }

    public boolean checkTimeOut() {
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

    public ArrayList<MessageEvent> getEvents() {
        ArrayList<MessageEvent> messageEvents = new ArrayList<>();
        setLatestTime();

        if (messageQueue != null) {
            try {
                MessageEvent messageEvent = messageQueue.poll(5, TimeUnit.SECONDS);
                if (messageEvent != null && messageQueue != null) {
                    messageEvents.add(messageEvent);
                    if (messageQueue.size() != 0) {
                        for (int i = 0; i < messageQueue.size(); i++) {
                            messageEvents.add(messageQueue.take());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("getEvents() exception : {}", e.getMessage());
            }
        }

        return messageEvents;
    }

    public void removeAll() {
        if (messageQueue != null) {
            messageQueue.clear();
            messageQueue = null;
        }
    }


}
