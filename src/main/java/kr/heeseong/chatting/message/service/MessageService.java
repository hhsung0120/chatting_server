package kr.heeseong.chatting.message.service;

import kr.heeseong.chatting.eventenum.ChattingRoomType;
import kr.heeseong.chatting.eventenum.MessageEventType;
import kr.heeseong.chatting.exceptions.BadArgumentException;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.message.model.MessageEvent;
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

    /**
     * 일반 메시지, 유저가 send 로 보내는 메시지
     *
     * @param messageEvent
     * @param chattingRoomData
     * @throws Exception
     */
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
            sendMessageToPerson(messageEvent.getFromUserIdx(), messageEvent);
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

    /**
     * 1:1 귓속말 기능
     *
     * @param messageEvent
     * @throws Exception
     */
    public void sendDirectMessage(MessageEvent messageEvent) throws Exception {
        sendMessageToPerson(messageEvent.getToUserIdx(), messageEvent);
        sendMessageToPerson(messageEvent.getFromUserIdx(), messageEvent);
    }

    /**
     * 승인 완료 된 메시지 전체 유저에게 전송
     *
     * @param messageEvent
     * @param chattingRoomData
     * @throws Exception
     */
    public void sendApproveMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendMessageToPerson(messageEvent.getFromUserIdx(), messageEvent);

        MessageEvent newMessageEvent = MessageEvent.setMessageCloneEvent(messageEvent, MessageEventType.NORMAL_MSG);
        sendMessageToAllUsers(chattingRoomData, newMessageEvent);
    }

    /**
     * 방 입장 알림
     *
     * @param messageEvent
     * @param chattingRoomData
     * @throws Exception
     */
    public void sendEnterUserMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendMessageToRoom(chattingRoomData, messageEvent);
    }

    /**
     * 관리자에게 메시지 보내기
     *
     * @param room
     * @param messageEvent
     * @throws Exception
     */
    public void sendMessageToAdmin(ChattingRoomData room, MessageEvent messageEvent) throws Exception {
        for (Long userSeq : room.getInternalUsers()) {
            if (userSeq.equals(messageEvent.getFromUserIdx()) || userSeq.equals(room.getAdminIdx())) {
                sendMessageToPerson(userSeq, messageEvent);
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
        for (Long userSeq : room.getInternalUsers()) {
            ChattingUserData user = chattingUserService.getChattingUser(userSeq);
            if (user.isAdmin()) {
                messageEvent.setEventType(MessageEventType.REQ_APPROVAL_MSG);
                sendMessageToPerson(userSeq, messageEvent);
            } else {
                if (user.getUserIdx().equals(messageEvent.getFromUserIdx())) {
                    MessageEvent myMessage = MessageEvent.setMessageCloneEvent(messageEvent, MessageEventType.WAIT_APPROVAL_MSG);
                    sendMessageToPerson(userSeq, myMessage);
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
        for (Long userSeq : chattingRoomData.getInternalUsers()) {
            sendMessageToPerson(userSeq, messageEvent);
        }
    }

    /**
     * 유저의 승인 요청 메시지를 거절
     * 유저에게 알려줌
     *
     * @param messageEvent
     * @throws Exception
     */
    public void sendRejectMessage(MessageEvent messageEvent) throws Exception {
        //TODO 관리자 인지 검사 해야지 .. ?
        ChattingUserData user = chattingUserService.getChattingUser(messageEvent.getFromUserIdx());
        user.postMessage(messageEvent);
    }

    /**
     * 방 관리자 -> 모든 유저에게 전체 메시지
     *
     * @param messageEvent
     * @param chattingRoomData
     * @throws Exception
     */
    public void sendAdminMessage(MessageEvent messageEvent, ChattingRoomData chattingRoomData) throws Exception {
        sendMessageToAllUsers(chattingRoomData, messageEvent);
    }


    /**
     * 방 입장 알림
     *
     * @param chattingRoomData
     * @param messageEvent
     * @throws Exception
     */
    private void sendMessageToRoom(ChattingRoomData chattingRoomData, MessageEvent messageEvent) throws Exception {
        for (Long userSeq : chattingRoomData.getInternalUsers()) {
            if (messageEvent.getFromUserIdx() != userSeq) {
                sendMessageToPerson(userSeq, messageEvent);
            }
        }
    }

    /**
     * 해당 유저의 큐에 메시지를 실제로 담는 로직
     *
     * @param userSeq
     * @param messageEvent
     * @throws Exception
     */
    private void sendMessageToPerson(Long userSeq, MessageEvent messageEvent) throws Exception {
        ChattingUserData user = chattingUserService.getChattingUser(userSeq);
        user.postMessage(messageEvent);
//        if (user != null) {
//            try {
//                user.postMessage(messageEvent);
//            } catch (Exception e) {
//                log.error("sendEventToPerson exception : {}", e.getMessage());
//                if (user.checkTimeOut()) {
//                    try {
//                        //순환참조 에러 때문에 잠시 주석 해둠 오류를 스로우해서 호출한 room 서비스에서 리이브 하도록 처리하자
//                        //chattingRoomService.leaveChatRoom(internalIdx, user.getProgramIdx(), null);
//                    } catch (Exception ex) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }
}
