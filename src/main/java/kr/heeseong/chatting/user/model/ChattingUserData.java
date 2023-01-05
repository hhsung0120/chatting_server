package kr.heeseong.chatting.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.heeseong.chatting.room.model.MessageEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Getter
public class ChattingUserData {

    private ChattingUser ChattingUser;

    @Setter
    private Long programIdx;
    private ArrayBlockingQueue<MessageEvent> messageQueue;
    private long latestMessageTime;
    private long DEFAULT_MESSAGE_TIMEOUT = 60 * 1000 * 2; // 2 minutes
    private long userTimeout = DEFAULT_MESSAGE_TIMEOUT;

    public ChattingUserData() {
    }

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

    public void postMessage(MessageEvent messageEventOld) {
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
    public ArrayList<MessageEvent> getEvents() {
        setLatestTime();
        ArrayList<MessageEvent> messageEventOlds = new ArrayList<>();
        if (messageQueue != null) {
            try {
                MessageEvent messageEventOld = messageQueue.poll(5000, TimeUnit.MILLISECONDS);
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
