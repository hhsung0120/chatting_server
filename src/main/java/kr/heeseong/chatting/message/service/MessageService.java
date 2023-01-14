package kr.heeseong.chatting.message.service;

import kr.heeseong.chatting.old.event_enum.ChattingRoomType;
import kr.heeseong.chatting.old.event_enum.MessageEventType;
import kr.heeseong.chatting.old.exceptions.BadArgumentException;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.room.model.EventManager;
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

    //2023-01-13 정리
    public void sendGeneralMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws BadArgumentException {

        Long fromUserIdx = messageEvent.getFromUserIdx();
        ChattingUserData chattingUserData = chattingUserService.getChattingUser(fromUserIdx);

        if (chattingRoomData.isBlackList(messageEvent.getFromUserIdx())) {
            messageEvent.setMessageEventType(MessageEventType.BLOCKED_MSG.getValue());
            sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
            return;
        }

        if (chattingRoomData.getChattingRoomType() == ChattingRoomType.MANY_TO_MANY.getValue()) {
            sendEventToRoom(fromUserIdx, messageEvent, true, chattingRoomData);
        } else if (chattingRoomData.getChattingRoomType() == ChattingRoomType.ONE_TO_MANY.getValue()) {
            if (chattingUserData != null && chattingUserData.isAdmin()) {
                sendEventToRoom(fromUserIdx, messageEvent, true, chattingRoomData);
            } else {
                sendEventToPerson(chattingRoomData.getAdminIdx(), messageEvent, chattingRoomData);
                sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent, chattingRoomData);
            }
        } else if (chattingRoomData.getChattingRoomType() == ChattingRoomType.APPROVAL.getValue()) {
            if (chattingUserData != null && chattingUserData.isAdmin()) {
                sendEventToRoom(fromUserIdx, messageEvent, true, chattingRoomData);
            } else {
                // normal user : send approval request to admin
                messageEvent.setMessageEventType(MessageEventType.REQ_APPROVAL_MSG.getValue());
                sendEventToPerson(chattingRoomData.getAdminIdx(), messageEvent, chattingRoomData);
                MessageEvent waitMessageEventOld = EventManager.cloneEvent(messageEvent);
                waitMessageEventOld.setMessageEventType(MessageEventType.WAIT_APPROVAL_MSG.getValue());
                sendEventToPerson(waitMessageEventOld.getFromUserIdx(), waitMessageEventOld, chattingRoomData);
            }
        } else {
            throw new BadArgumentException();
        }
    }

    public void sendDirectMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) {
        sendEventToPerson(messageEvent.getToUserIdx(), messageEvent, chattingRoomData);
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
    }

    public void sendApproveMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) {
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent, chattingRoomData);
        MessageEvent newMessageEvent = EventManager.cloneEvent(messageEvent);
        newMessageEvent.setMessageEventType(MessageEventType.NORMAL_MSG.getValue());
        sendEventToRoom(messageEvent.getFromUserIdx(), newMessageEvent, true, chattingRoomData);
    }

    public void sendEventToPerson(Long userIdx, MessageEvent messageEvent, ChattingRoomData room) {
        if (room != null) {
            for (Long keyIndex : room.getInternalUsers()) {
                ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
                if (userIdx == user.getUserIdx()) {
                    sendEventToPerson(keyIndex, messageEvent);
                }
            }
        }
    }

    public void sendEventToPerson(Long internalIdx, MessageEvent messageEvent) {
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

    public void sendMessageEvent(Long internalIdx, MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {

        if (messageEvent.getMessageEventType() == MessageEventType.ENTER_USER.getValue()) {
            sendEventToRoom(internalIdx, messageEvent, false, chattingRoomData);
        } else if (messageEvent.getMessageEventType() == MessageEventType.NORMAL_MSG.getValue()) {
            //sendMessage(internalIdx, messageEvent);
        } else if (messageEvent.getMessageEventType() == MessageEventType.DIRECT_MSG.getValue()) {
            //messageEventService.sendEventToPerson(messageEvent.getToUserIdx(), messageEvent, getChattingRoom(messageEvent.getProgramIdx()));
            //messageEventService.sendEventToPerson(internalIdx, messageEvent);
        } else {
            throw new Exception();
        }
    }

    private void sendEventToRoom(Long internalIdx, MessageEvent messageEvent, Boolean sendMyself, ChattingRoomData chattingRoomData) {
        if (chattingRoomData != null) {
            for (Long keyIndex : chattingRoomData.getInternalUsers()) {
                if (sendMyself || (internalIdx != keyIndex)) {
                    ChattingUserData user = chattingUserService.getChattingUser(keyIndex);
                    if (user != null) {
                        user.postMessage(messageEvent);
                    }
                }
            }
        }
    }

    public void sendRejectMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) {
        sendEventToPerson(messageEvent.getFromUserIdx(), messageEvent);
    }

    public void sendAdminMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) {
        sendEventToRoom(messageEvent.getFromUserIdx(), messageEvent, true, chattingRoomData);
    }
}