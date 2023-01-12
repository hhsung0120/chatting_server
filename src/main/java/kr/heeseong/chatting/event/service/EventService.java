package kr.heeseong.chatting.event.service;

import kr.heeseong.chatting.message.service.MessageEventService;
import kr.heeseong.chatting.old.exceptions.BadArgumentException;
import kr.heeseong.chatting.old.exceptions.ChatRoomNotExistException;
import kr.heeseong.chatting.old.exceptions.UserExistException;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import kr.heeseong.chatting.user.model.ChattingUserData;
import kr.heeseong.chatting.user.service.ChattingUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventService {

    private final ChattingRoomService chattingRoomService;
    private final ChattingUserService chattingUserService;
    private final MessageEventService messageEventService;

    public ChattingRoom enterChattingRoom(ChattingRoom chattingRoom) throws Exception {
        //chattingRoom.setInternalIdx(chattingRoom.getUserIdx());

        //채팅 방 존재 확인
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(chattingRoom.getChattingRoomSeq());
        if (chattingRoomData == null) {
            chattingRoomData = chattingRoomService.createChattingRoom(chattingRoom);
        }

        //채팅방 유저 셋팅
        ChattingUserData chattingUserData;
        try {
            chattingUserData = chattingUserService.setChattingUser(chattingRoom.getChattingUser());
        } catch (Exception e) {
            log.error("setChattingUser exception : {}", e.getMessage());
            throw e;
        }

        if (!chattingRoomData.addUser(chattingUserData.getChattingUser())) {
            log.error("room to add user exception : {}", chattingUserData);
            throw new UserExistException();
        }

        MessageEvent messageEvent = new MessageEvent(chattingRoom);
        messageEventService.sendMessageEvent(chattingUserData.getUserIdx(), messageEvent, chattingRoomData);

        return chattingRoom;
    }

    public MessageEvent sendGeneralMessage(MessageEvent messageEvent) throws Exception {
        //채팅 방 존재 확인
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(messageEvent.getChattingRoomSeq());
        if (chattingRoomData == null) {
            throw new ChatRoomNotExistException();
        }

        messageEventService.sendGeneralMessage(messageEvent, chattingRoomData);

        return messageEvent;
    }
}
