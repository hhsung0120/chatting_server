package kr.heeseong.chatting.user.service;

import kr.heeseong.chatting.exceptions.UnauthorizedException;
import kr.heeseong.chatting.exceptions.UserNotExistException;
import kr.heeseong.chatting.user.model.ChattingUser;
import kr.heeseong.chatting.user.model.ChattingUserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChattingUserService {

    //채팅 유저 리스트
    private ConcurrentHashMap<Long, ChattingUserData> chattingUsers = new ConcurrentHashMap<>();

    /**
     * 채팅 유저 조회
     *
     * @param userSeq
     * @return
     * @throws UserNotExistException
     */
    public ChattingUserData getChattingUser(Long userSeq) throws UserNotExistException {

        ChattingUserData chattingUserData = chattingUsers.get(userSeq);
        if (chattingUserData == null) {
            log.error("chattingUserData is null / userSeq : {}", userSeq);
            throw new UserNotExistException();
        }

        return chattingUserData;
    }

    /**
     * 유저 리스트
     *
     * @return
     */
    public ConcurrentHashMap<Long, ChattingUserData> getChattingUsers() {
        return chattingUsers;
    }

    /**
     * 채팅 유저 셋팅
     *
     * @param chattingUser
     * @return
     */
    public ChattingUserData setChattingUser(ChattingUser chattingUser) {

        WeakReference<ChattingUserData> userRef = new WeakReference<>(new ChattingUserData(chattingUser));
        ChattingUserData chattingUserData = userRef.get();

        chattingUsers.put(chattingUser.getUserIdx(), chattingUserData);
        return chattingUserData;
    }

    /**
     * 채팅방 어드민인지 확인
     *
     * @param userIdx
     * @throws Exception
     */
    public void checkAdmin(Long userIdx) throws Exception {
        ChattingUserData user = getChattingUser(userIdx);
        if (!user.isAdmin()) {
            log.error("invalid admin user : {}", user);
            throw new UnauthorizedException();
        }
    }

    public void checkUsersTimeout() {
        Iterator<Map.Entry<Long, ChattingUserData>> iter = getChattingUsers().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, ChattingUserData> userEntry = iter.next();
            ChattingUserData user = userEntry.getValue();
            if (user != null) {
                if (user.checkTimeOut()) {
                    try {
                        //TODO : 이건 기다려봐..
                        //leaveChatRoom(user.getChattingRoomSeq(), user.getProgramIdx(), iter);
                    } catch (Exception e) {
                        log.error("checkUsersTimeout exception : {}", e.getMessage());
                    }
                }
            }
        }
    }
}
