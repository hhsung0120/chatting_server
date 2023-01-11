package kr.heeseong.chatting.user.service;

import kr.heeseong.chatting.user.model.ChattingUser;
import kr.heeseong.chatting.user.model.ChattingUserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChattingUserService {

    //채팅 유저 리스트
    private ConcurrentHashMap<Long, ChattingUserData> chattingUsers = new ConcurrentHashMap<>();

    public void setCattingUsers(Long userSeq, ChattingUserData chattingUserData) {
        chattingUsers.put(userSeq, chattingUserData);
    }

    public ChattingUserData getChattingUser(Long userSeq) {
        return chattingUsers.get(userSeq);
    }

    public ConcurrentHashMap<Long, ChattingUserData> getChattingUsers() {
        return chattingUsers;
    }

    public void chattingUserRemove(Long userSeq) {
        chattingUsers.remove(userSeq);
    }

    public ChattingUserData setChattingUser(ChattingUser chattingUser) {
        WeakReference<ChattingUserData> userRef = new WeakReference<>(new ChattingUserData(chattingUser));
        ChattingUserData chattingUserData = userRef.get();

        chattingUsers.put(chattingUser.getUserIdx(), chattingUserData);
        return chattingUserData;
    }
}
