package kr.heeseong.chatting.user.model;

import kr.heeseong.chatting.room.model.MessageEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class ChattingUserData {

    private ChattingUser ChattingUser;

    @Setter
    private Long programIdx;
    private ArrayBlockingQueue<MessageEvent> messageQueue;
    private long latestMessageTime;
    private long DEFAULT_MESSAGE_TIMEOUT = 60 * 1000 * 2; // 2 minutes
    private long userTimeout = DEFAULT_MESSAGE_TIMEOUT;

    public ChattingUserData(ChattingUser ChattingUser) {
        this.ChattingUser = ChattingUser;
        messageQueue = new ArrayBlockingQueue<>(10);
        latestMessageTime = System.currentTimeMillis();
    }

    public long getUserIdx() {
        return ChattingUser.getUserIdx();
    }

    public long getInternalIdx() {
        return ChattingUser.getInternalIdx();
    }

    public String getUserId() {
        return ChattingUser.getUserId();
    }

    public String getUserName() {
        return ChattingUser.getUserName();
    }

    public boolean isAdmin() {
        return ChattingUser.isAdmin();
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
            userTimeout = 60000;
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
