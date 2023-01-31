package kr.heeseong.chatting.message.service;

import kr.heeseong.chatting.eventenum.ChattingRoomType;
import kr.heeseong.chatting.eventenum.MessageEventType;
import kr.heeseong.chatting.exceptions.BadArgumentException;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.user.model.ChattingUserData;
import kr.heeseong.chatting.user.service.ChattingUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChattingUserService chattingUserService;

    public void sendGeneralMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {

        messageEvent.setEventType(MessageEventType.NORMAL_MSG);
        Long fromUserIdx = messageEvent.getFromUserIdx();

        ChattingUserData chattingUserData = chattingUserService.getChattingUser(fromUserIdx);
        if (chattingUserData.isAdmin()) {
            sendMessageToAllUsers(chattingRoomData, messageEvent);
            return;
        }

        if (chattingRoomData.isBlockUser(messageEvent.getFromUserIdx())) {
            messageEvent.setEventType(MessageEventType.BLOCKED_MSG);
            sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
            return;
        }

        if (chattingRoomData.getChattingRoomType() == ChattingRoomType.MANY_TO_MANY) {
            sendMessageToAllUsers(chattingRoomData, messageEvent);
            return;
        }

        if (chattingRoomData.getChattingRoomType() == ChattingRoomType.ONE_TO_MANY) {
            sendMessageToAdmin(chattingRoomData, messageEvent);
            return;
        }

        if (chattingRoomData.getChattingRoomType() == ChattingRoomType.APPROVAL) {
            sendApprovalRequestMessageToAdmin(chattingRoomData, messageEvent);
            return;
        }

        throw new BadArgumentException();
    }

    public void sendDirectMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendEventToPerson(messageEvent.getToUserIdx(), messageEvent, chattingRoomData);
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
    }

    public void sendApproveMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent, chattingRoomData);

        MessageEvent newMessageEvent = MessageEvent.setMessageCloneEvent(messageEvent, MessageEventType.NORMAL_MSG);
        sendMessageToAllUsers(chattingRoomData, newMessageEvent);
    }

    public void sendEnterUserMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
//        sendMessageToAllUsers(chattingRoomData, messageEvent);
        sendEventToRoom(messageEvent, false, chattingRoomData);
    }

    /**
     * 관리자에게 메시지 보내기
     *
     * @param room
     * @param messageEvent
     * @throws Exception
     */
    public void sendMessageToAdmin(ChattingRoomData room, MessageEvent messageEvent) throws Exception {
        for (Long keyIndex : room.getInternalUsers()) {
            ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
            if (user.getUserIdx().equals(messageEvent.getFromUserIdx()) || user.getUserIdx().equals(room.getAdminIdx())) {
                sendEventToPerson(keyIndex, messageEvent);
            }
        }
    }

    /**
     * 관리자에게 메시지 승인 요청 보내기
     *
     * @param room
     * @param messageEvent
     * @throws Exception
     */
    public void sendApprovalRequestMessageToAdmin(ChattingRoomData room, MessageEvent messageEvent) throws Exception {
        for (Long keyIndex : room.getInternalUsers()) {
            ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
            if (user.isAdmin()) {
                messageEvent.setEventType(MessageEventType.REQ_APPROVAL_MSG);
                sendEventToPerson(keyIndex, messageEvent);
            } else {
                if (user.getUserIdx().equals(messageEvent.getFromUserIdx())) {
                    MessageEvent myMessage = MessageEvent.setMessageCloneEvent(messageEvent, MessageEventType.WAIT_APPROVAL_MSG);
                    sendEventToPerson(keyIndex, myMessage);
                }
            }
        }
    }

    public void sendEventToPerson(Long userIdx, MessageEvent messageEvent, ChattingRoomData room) throws Exception {
        for (Long keyIndex : room.getInternalUsers()) {
            ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
            if (userIdx.equals(user.getUserIdx())) {
                sendEventToPerson(keyIndex, messageEvent);
            }
        }
    }

    public void sendEventToPerson(Long internalIdx, MessageEvent messageEvent) throws Exception {
        ChattingUserData user = chattingUserService.getChattingUser(internalIdx);
        if (user != null) {
            try {
                user.postMessage(messageEvent);
            } catch (Exception e) {
                log.error("sendEventToPerson exception : {}", e.getMessage());
                if (user.checkTimeOut()) {
                    try {
                        //순환참조 에러 때문에 잠시 주석 해둠 오류를 스로우해서 호출한 room 서비스에서 리이브 하도록 처리하자
                        //chattingRoomService.leaveChatRoom(internalIdx, user.getProgramIdx(), null);
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 채팅방 모두에게 메시지
     *
     * @param chattingRoomData
     * @param messageEvent
     * @throws Exception
     */
    private void sendMessageToAllUsers(ChattingRoomData chattingRoomData, MessageEvent messageEvent) throws Exception {
        for (Long keyIndex : chattingRoomData.getInternalUsers()) {
            ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
            if (user != null) {
                user.postMessage(messageEvent);
            }
        }
    }

    private void sendEventToRoom(MessageEvent messageEvent, Boolean sendMyself, ChattingRoomData chattingRoomData) throws Exception {
        for (Long keyIndex : chattingRoomData.getInternalUsers()) {
            if (sendMyself || (messageEvent.getFromUserIdx() != keyIndex)) {
                ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
                if (user != null) {
                    user.postMessage(messageEvent);
                }
            }
        }
    }

    public void sendRejectMessage(MessageEvent messageEvent) throws Exception {
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
    }

    public void sendAdminMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendMessageToAllUsers(chattingRoomData, messageEvent);
    }
}
