package kr.heeseong.chatting.event.service;

import kr.heeseong.chatting.eventenum.MessageEventType;
import kr.heeseong.chatting.exceptions.ChatRoomNotExistException;
import kr.heeseong.chatting.exceptions.UserExistException;
import kr.heeseong.chatting.message.service.MessageService;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.message.model.MessageEvent;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import kr.heeseong.chatting.user.model.ChattingUser;
import kr.heeseong.chatting.user.model.ChattingUserData;
import kr.heeseong.chatting.user.service.ChattingUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventService {

    private final ChattingRoomService chattingRoomService;
    private final ChattingUserService chattingUserService;
    private final MessageService messageService;

    public ChattingRoom createChattingRoom(ChattingRoom chattingRoom) throws Exception {
        log.info("createChattingRoom : {}", chattingRoom);

        //채팅 방 존재 확인
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(chattingRoom.getChattingRoomSeq());
        if (chattingRoomData == null) {
            chattingRoomData = chattingRoomService.createChattingRoom(chattingRoom);
        }

        try {
            enterChattingRoom(chattingRoom);
        } catch (Exception e) {
            log.error("enter room exception : {}", e.getMessage());
            throw e;
        }

        MessageEvent messageEvent = new MessageEvent(chattingRoom);
        messageService.sendEnterUserMessage(messageEvent, chattingRoomData);

        return chattingRoom;
    }

    /**
     * 채팅 방 입장
     *
     * @param chattingRoom
     * @return
     * @throws Exception
     */
    public ChattingRoom enterChattingRoom(ChattingRoom chattingRoom) throws Exception {
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(chattingRoom.getChattingRoomSeq());
        if (chattingRoomData == null) {
            chattingRoomData = chattingRoomService.createChattingRoom(chattingRoom);
        }

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

        return chattingRoom;
    }

    /**
     * 유저가 센드로 보내는 일반 메시지
     *
     * @param messageEvent
     * @return
     * @throws Exception
     */
    public MessageEvent sendGeneralMessage(MessageEvent messageEvent) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(messageEvent.getChattingRoomSeq());
        messageService.sendGeneralMessage(messageEvent, chattingRoomData);

        return messageEvent;
    }

    /**
     * 유저 -> 유저 다이렉트로 보내는 1:1 메시지
     *
     * @param messageEvent
     * @return
     * @throws Exception
     */
    public MessageEvent sendDirectEvent(MessageEvent messageEvent) throws Exception {

        checkRoomExistence(messageEvent.getChattingRoomSeq());

        messageEvent.setEventType(MessageEventType.DIRECT_MSG);
        messageService.sendDirectMessage(messageEvent);

        return messageEvent;
    }

    /**
     * 메시지 승인 완료 된 메시지 전달
     *
     * @param messageEvent
     * @return
     * @throws Exception
     */
    public MessageEvent sendApproveMessage(MessageEvent messageEvent) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(messageEvent.getChattingRoomSeq());

        messageEvent.setEventType(MessageEventType.APPROVED_MSG);
        messageService.sendApproveMessage(messageEvent, chattingRoomData);

        return messageEvent;
    }

    /**
     * 승인 요청 메시지 거절 메시지
     *
     * @param messageEvent
     * @return
     * @throws Exception
     */
    public MessageEvent sendRejectMessage(MessageEvent messageEvent) throws Exception {

        checkRoomExistence(messageEvent.getChattingRoomSeq());

        messageEvent.setEventType(MessageEventType.REJECTED_MSG);
        messageService.sendRejectMessage(messageEvent);

        return messageEvent;
    }

    /**
     * 관리자 -> 유저 전체 메시지
     *
     * @param messageEvent
     * @return
     * @throws Exception
     */
    public MessageEvent sendAdminMessage(MessageEvent messageEvent) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(messageEvent.getChattingRoomSeq());

        messageEvent.setEventType(MessageEventType.ADMIN_MSG);
        messageService.sendAdminMessage(messageEvent, chattingRoomData);

        return messageEvent;
    }

    /**
     * 유저 차단
     *
     * @param messageEvent
     * @throws Exception
     */
    public void addBlockUser(MessageEvent messageEvent) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(messageEvent.getChattingRoomSeq());

        chattingUserService.checkAdmin(messageEvent.getFromUserIdx());

        chattingRoomData.addBlockList(messageEvent.getToUserIdx());
    }

    /**
     * 차단 해제
     *
     * @param messageEvent
     * @throws Exception
     */
    public void removeBlockUser(MessageEvent messageEvent) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(messageEvent.getChattingRoomSeq());

        chattingUserService.checkAdmin(messageEvent.getFromUserIdx());

        chattingRoomData.removeBlockList(messageEvent.getToUserIdx());
    }

    /**
     * 채팅방 유저 목록
     *
     * @param chattingRoomSeq
     * @return
     * @throws Exception
     */
    public ArrayList<ChattingUser> chattingRoomUserList(Long chattingRoomSeq) throws Exception {

        ChattingRoomData chattingRoomData = getChattingRoomData(chattingRoomSeq);

        ArrayList<ChattingUser> userList = new ArrayList<>();
        for (Map.Entry<Long, ChattingUser> userEntry : chattingRoomData.getUserList().entrySet()) {
            userList.add(userEntry.getValue());
        }

        return userList;
    }

    /**
     * 채팅 방 정보
     *
     * @param chattingRoomSeq
     * @return
     * @throws ChatRoomNotExistException
     */
    private ChattingRoomData getChattingRoomData(Long chattingRoomSeq) throws ChatRoomNotExistException {
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(chattingRoomSeq);
        if (chattingRoomData == null) {
            throw new ChatRoomNotExistException();
        }

        return chattingRoomData;
    }

    /**
     * 채팅방 존재 확인
     *
     * @param chattingRoomSeq
     * @throws ChatRoomNotExistException
     */
    private void checkRoomExistence(Long chattingRoomSeq) throws ChatRoomNotExistException {
        ChattingRoomData chattingRoomData = chattingRoomService.getChattingRoom(chattingRoomSeq);
        if (chattingRoomData == null) {
            throw new ChatRoomNotExistException();
        }
    }
}
