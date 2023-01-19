package kr.heeseong.chatting.room.service;

import kr.heeseong.chatting.eventenum.MessageEventType;
import kr.heeseong.chatting.exceptions.BadArgumentException;
import kr.heeseong.chatting.exceptions.ChatRoomNotExistException;
import kr.heeseong.chatting.exceptions.UnauthorizedException;
import kr.heeseong.chatting.exceptions.UserNotExistException;
import kr.heeseong.chatting.message.service.MessageService;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.room.model.EventManager;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.user.model.ChattingUser;
import kr.heeseong.chatting.user.model.ChattingUserData;
import kr.heeseong.chatting.user.service.ChattingUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomService {

    //채팅 방
    private ConcurrentHashMap<Long, ChattingRoomData> chattingRooms = new ConcurrentHashMap<>();

    private final ChattingUserService chattingUserService;

    /**
     * 채팅방 존재 확인
     * @param chattingRoomSeq
     * @return ChattingRoomData
     */
    public ChattingRoomData getChattingRoom(Long chattingRoomSeq) {
        return chattingRooms.get(chattingRoomSeq);
    }

    public ChattingRoomData createChattingRoom(ChattingRoom chattingRoom) {
        WeakReference<ChattingRoomData> chatRoomRef = new WeakReference<>(new ChattingRoomData());
        ChattingRoomData chattingRoomData = chatRoomRef.get();
        chattingRoomData.setChattingRoom(chattingRoom);

        chattingRooms.put(chattingRoom.getChattingRoomSeq(), chattingRoomData);
        return chattingRoomData;
    }

    public ArrayList<ChattingRoom> listChatRooms() {
        ArrayList<ChattingRoom> roomList = new ArrayList<ChattingRoom>();

        for (Map.Entry<Long, ChattingRoomData> roomEntry : chattingRooms.entrySet()) {
            ChattingRoomData room = roomEntry.getValue();
            if (room != null) {
                roomList.add(room.getChattingRoom());
            }
        }

        return roomList;
    }

    public ArrayList<MessageEvent> getNewEvents(Long internalIdx) throws Exception {
        ChattingUserData user = chattingUserService.getChattingUser(internalIdx);
        if (user == null) {
            throw new UserNotExistException();
        }

        return user.getEvents();
    }
}
