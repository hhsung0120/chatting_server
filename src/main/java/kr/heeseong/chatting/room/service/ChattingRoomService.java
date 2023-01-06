package kr.heeseong.chatting.room.service;

import kr.heeseong.chatting.old.event_enum.ChattingRoomType;
import kr.heeseong.chatting.old.event_enum.MessageEventType;
import kr.heeseong.chatting.old.exceptions.*;
import kr.heeseong.chatting.old.mapper.ChattingMapper;
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

    //채팅 유저
    private ConcurrentHashMap<Long, ChattingUserData> chattingUsers = new ConcurrentHashMap<>();

    //채팅 방 인덱스
    private Long chattingRoomSeq = 1L;
    private final ChattingMapper chattingMapper;
    private final ChattingUserService chattingUserService;

    public ChattingRoom enterChattingRoom(ChattingRoom chattingRoom) throws Exception {

        //ChattingUsers chattingUsers = new ChattingUsers(chattingRoom.getUserIdx(), chattingRoom.getUserId(), chattingRoom.getUserName(), chattingRoom.isAdmin());

//        MessageEvent roomMessageEvent = new MessageEvent(
//                MessageEventType.ENTER_USER.getValue()
//                , resultChattingRoom.getProgramIdx()
//                , chattingRoom.getChattingUser().getUserIdx()
//                , chattingRoom.getChattingUser().getUserIdx()
//                , chattingRoom.getChattingUser().getUserName()
//                , "내가 접속했다."
//                , chattingRoom.getChattingUser().getUserId());
//
//        System.out.println("===================");
//        System.out.println(chattingUsers);
//        System.out.println(resultChattingRoom);
//        System.out.println(roomMessageEvent);
//        System.out.println("===================");
//
//        chattingMapper.insertEvent(roomMessageEvent);
        return this.createChattingRoomAndChattingUser(chattingRoom);
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

    public ChattingRoom getChatRoom(int roomIdx) {
        ChattingRoomData room = chattingRooms.get(roomIdx);
        if (room != null) {
            return room.getChattingRoom();
        }
        return null;
    }

    public ArrayList<ChattingUser> listUsers(Long roomIdx) {
        ArrayList<ChattingUser> userList = new ArrayList<>();

        ChattingRoomData chattingRoomData = chattingRooms.get(roomIdx);
        if (chattingRoomData == null) {
            return null;
        }

        for (Map.Entry<Long, ChattingUser> userEntry : chattingRoomData.getUserList().entrySet()) {
            userList.add(userEntry.getValue());
        }

        return userList;
    }

    public void leaveChatRoom(Long programIdx, Long userIdx, Long internalIdx) throws Exception {
        this.goleaveChatRoom(internalIdx, programIdx, null);
        MessageEvent roomMessageEvent = new MessageEvent(MessageEventType.LEAVE_USER.getValue(), programIdx, userIdx, 0L, "", "", "");
        //chattingMapper.insertEvent(roomMessageEvent);
    }


    public void addBlackList(long internalIdx, Long userIdx, Long programIdx, long blackUserIdx) throws Exception {
        //TODO 메모리에 담는 구조임 현재, 디비에도 담고 꺼낼 수 있도록 개선 해야함
        this.addBlackList(internalIdx, programIdx, blackUserIdx);

        MessageEvent roomMessageEvent = new MessageEvent(MessageEventType.ADD_BLACKLIST.getValue(), programIdx, blackUserIdx, userIdx, "", "", "");
        //chattingMapper.insertEvent(roomMessageEvent);
    }

    public void removeBlackList(long internalIdx, Long userIdx, Long programIdx, long blackUserIdx) throws Exception {
        this.removeBlackList(internalIdx, programIdx, blackUserIdx);
        MessageEvent messageEventOld = new MessageEvent(MessageEventType.REMOVE_BLACKLIST.getValue(), programIdx, blackUserIdx, userIdx, "", "", "");
        //chattingMapper.insertEvent(messageEventOld);
    }

    public MessageEvent sendEvent(Long internalIdx, MessageEvent messageEvent) throws Exception {
        //chattingMapper.insertEvent(messageEventOld);
        messageEvent.setIdx(messageEvent.getIdx());

        this.sendMessageEvent(internalIdx, messageEvent);

        return messageEvent;
    }

    public ArrayList<MessageEvent> getNewEvents(Long internalIdx) throws Exception {
        ChattingUserData user = chattingUsers.get(internalIdx);
        if (user == null) {
            throw new UserNotExistException();
        }

        return user.getEvents();
    }

    private ChattingRoom createChattingRoomAndChattingUser(ChattingRoom chattingRoom) throws Exception {

        ChattingRoomData chattingRoomData = chattingRooms.get(chattingRoom.getChattingRoomSeq());
        if (chattingRoomData == null) {
            chattingRoomData = createChattingRoom(chattingRoom);
        }

        ChattingUserData chattingUserData;
        try {
            chattingUserData = setChattingUser(chattingRoom.getChattingUser());
        } catch (Exception e) {
            log.error("setChattingUser exception : {}", e.getMessage());
            throw e;
        }

        if (!chattingRoomData.addUser(chattingUserData.getChattingUser())) {
            throw new UserExistException();
        }

        MessageEvent messageEvent =
                MessageEvent.enterRoomEventBuilder()
                        .chattingRoom(chattingRoom)
                        .build();

        sendMessageEvent(chattingUserData.getInternalIdx(), messageEvent);

        chattingUserData.setProgramIdx(chattingRoom.getChattingRoomSeq());
        chattingRoom.setInternalIdx(chattingUserData.getInternalIdx());

        //chattingMapper.insertEvent(messageEvent);
        return chattingRoom;
    }

    public int goleaveChatRoom(long internalIdx, Long roomIdx, Iterator<Map.Entry<Long, ChattingUserData>> userIteration) throws Exception {
        if (roomIdx != -1) {
            ChattingRoomData chatRoomManager = chattingRooms.get(roomIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeUser(internalIdx);

            ChattingUserData user = chattingUsers.get(internalIdx);
            if (user != null) {
                removeUser(internalIdx, userIteration);
            }

            if (chatRoomManager.getInternalUsers().size() == 0) {
                removeChatRoom(internalIdx, roomIdx);
            } else {
                MessageEvent messageEventOld = EventManager.makeLeaveRoomEvent(roomIdx, user.getUserIdx());
                sendMessageEvent(internalIdx, messageEventOld);
                //chattingMapper.insertEvent(messageEventOld);
            }
        } else {
            throw new BadArgumentException();
        }

        return 0;
    }

    public Long[] getBlackList(long internalIdx, int roomIdx) throws Exception {
        checkAdmin(internalIdx);

        ChattingRoomData chatRoomManager = chattingRooms.get(roomIdx);
        if (chatRoomManager == null) {
            return null;
        }

        return chatRoomManager.getBlackListArray();
    }

    private void addBlackList(long internalIdx, Long programIdx, long blackUser) throws Exception {
        checkAdmin(internalIdx);

        if (programIdx != -1) {
            ChattingRoomData chatRoomManager = chattingRooms.get(programIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }
            chatRoomManager.addBlackList(blackUser);
        } else {
            throw new BadArgumentException();
        }
    }

    private void removeBlackList(long internalIdx, Long programIdx, long blackUser) throws Exception {
        checkAdmin(internalIdx);

        if (programIdx != -1) {
            ChattingRoomData chatRoomManager = chattingRooms.get(programIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeBlackList(blackUser);
        }
    }

    private void sendMessageEvent(Long internalIdx, MessageEvent messageEvent) throws Exception {

        if (messageEvent.getMessageEventType() == MessageEventType.NORMAL_MSG.getValue()) {
            sendMessage(internalIdx, messageEvent);
        } else if (messageEvent.getMessageEventType() == MessageEventType.ENTER_USER.getValue()) {
            sendEventToRoom(internalIdx, messageEvent, false);
        } else if (messageEvent.getMessageEventType() == MessageEventType.LEAVE_USER.getValue()) {
            sendEventToRoom(internalIdx, messageEvent, false);
        } else if (messageEvent.getMessageEventType() == MessageEventType.APPROVED_MSG.getValue()) {
            sendEventToPerson(messageEvent.getProgramIdx(), messageEvent.getFromUserIdx(), messageEvent);
            MessageEvent newMessageEvent = EventManager.cloneEvent(messageEvent);
            newMessageEvent.setMessageEventType(MessageEventType.NORMAL_MSG.getValue());
            sendEventToRoom(internalIdx, newMessageEvent);
        } else if (messageEvent.getMessageEventType() == MessageEventType.REJECTED_MSG.getValue()) {
            sendEventToPerson(messageEvent.getProgramIdx(), messageEvent.getFromUserIdx(), messageEvent);
        } else if (messageEvent.getMessageEventType() == MessageEventType.DIRECT_MSG.getValue()) {
            sendEventToPerson(messageEvent.getProgramIdx(), messageEvent.getToUserIdx(), messageEvent);
            sendEventToPerson(internalIdx, messageEvent);
        } else if (messageEvent.getMessageEventType() == MessageEventType.ADMIN_MSG.getValue()) {
            sendEventToRoom(internalIdx, messageEvent, true);
        } else {
            throw new Exception();
        }
    }

    public ChattingUserData setChattingUser(ChattingUser chattingUser) {
        chattingUser.setInternalIdx(chattingRoomSeq++);

        WeakReference<ChattingUserData> userRef = new WeakReference<>(new ChattingUserData(chattingUser));
        ChattingUserData ChattingUserData = userRef.get();

        this.chattingUsers.put(chattingUser.getInternalIdx(), ChattingUserData);

        return ChattingUserData;
    }

    public int removeUser(long internalIdx, Iterator<Map.Entry<Long, ChattingUserData>> userIteration) {
        ChattingUserData user = chattingUsers.get(internalIdx);
        if (user == null) {
            return -1;
        }

        user.removeAll();

        if (userIteration != null) {
            userIteration.remove();
        } else {
            chattingUsers.remove(internalIdx);
        }

        return 0;
    }


    public int leaveChatRoom(long internalIdx, Long roomIdx, Iterator<Map.Entry<Long, ChattingUserData>> userIteration) throws Exception {
        if (roomIdx != -1) {
            ChattingRoomData chatRoomManager = chattingRooms.get(roomIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeUser(internalIdx);

            ChattingUserData user = chattingUsers.get(internalIdx);
            if (user != null) {
                this.removeUser(internalIdx, userIteration);
            }

            if (chatRoomManager.getInternalUsers().size() == 0) {
                this.removeChatRoom(internalIdx, roomIdx);
            } else {
                MessageEvent messageEventOld = EventManager.makeLeaveRoomEvent(roomIdx, user.getUserIdx());
                this.sendEvent(internalIdx, messageEventOld);
                //chattingMapper.insertEvent(messageEventOld);
            }
        } else {
            throw new BadArgumentException();
        }

        return 0;
    }

    private void removeChatRoom(long internalIdx, Long roomIdx) throws Exception {
        ChattingRoomData chatRoomManager = chattingRooms.get(roomIdx);
        if (chatRoomManager == null) {
            throw new ChatRoomNotExistException();
        }

        chattingRooms.remove(roomIdx);

        MessageEvent messageEventOld = EventManager.removeChatRoomEvent(roomIdx);
        sendEvent(internalIdx, messageEventOld);
        //chattingMapper.insertEvent(messageEventOld);
    }

    private void checkAdmin(long internalIdx) throws Exception {
        ChattingUserData user = chattingUsers.get(internalIdx);
        log.info("checkAdmin {}", user);
        if (user == null || !user.isAdmin()) {
            throw new UnauthorizedException();
        }
    }

    private ChattingRoomData createChattingRoom(ChattingRoom chattingRoom) throws Exception {

        if (chattingRooms.get(chattingRoom.getChattingRoomSeq()) != null) {
            log.error("chatting room exist exception / chattingRoomSeq : {}", chattingRoom.getChattingRoomSeq());
            throw new ChatRoomExistException();
        }

        WeakReference<ChattingRoomData> chatRoomRef = new WeakReference<>(new ChattingRoomData());
        ChattingRoomData ChattingRoomData = chatRoomRef.get();
        ChattingRoomData.setChattingRoom(chattingRoom);

        chattingRooms.put(ChattingRoomData.getChattingRoomSeq(), ChattingRoomData);

//        if (log) {
//            MessageEvent messageEvent = EventManager.makeCreateRoomEvent(chattingRoom);
//            //sendEvent(internalIdx, event);
//            chattingMapper.insertEvent(messageEvent);
//        }

        return ChattingRoomData;
    }

    private void sendEventToPerson(Long roomIdx, long userIdx, MessageEvent messageEventOld) {
        ChattingRoomData room = chattingRooms.get(roomIdx);
        if (room != null) {
            for (Long keyIndex : room.getInternalUsers()) {
                ChattingUserData user = chattingUsers.get(keyIndex);
                if (userIdx == user.getUserIdx()) {
                    sendEventToPerson(keyIndex, messageEventOld);
                }
            }
        }
    }

    private void sendEventToRoom(Long internalIdx, MessageEvent messageEvent, boolean sendMyself) {
        ChattingRoomData room = chattingRooms.get(messageEvent.getProgramIdx());
        if (room != null) {
            for (Long keyIndex : room.getInternalUsers()) {
                if (sendMyself || (internalIdx != keyIndex)) {
                    ChattingUserData user = chattingUsers.get(keyIndex);
                    if (user != null) {
                        user.postMessage(messageEvent);
                    }
                }
            }
        }
    }

    private void sendEventToRoom(long internalIdx, MessageEvent messageEventOld) {
        sendEventToRoom(internalIdx, messageEventOld, true);
    }

    private void sendEventToPerson(long internalIdx, MessageEvent messageEventOld) {
        ChattingUserData user = chattingUsers.get(internalIdx);
        if (user != null) {
            try {
                //메시지 담는 구간
                user.postMessage(messageEventOld);
            } catch (Exception e) {
                if (user.checkTimeOut()) {
                    try {
                        leaveChatRoom(internalIdx, user.getProgramIdx(), null);
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    public void sendMessage(Long internalIdx, MessageEvent messageEventOld) throws Exception {

        ChattingRoomData room = chattingRooms.get(messageEventOld.getProgramIdx());
        if (room != null) {
            ChattingUserData user;

            if (room.isBlackList(messageEventOld.getFromUserIdx())) {
                messageEventOld.setMessageEventType(MessageEventType.BLOCKED_MSG.getValue());
                sendEventToPerson(internalIdx, messageEventOld);
                return;
            }

            if (room.getChattingRoomType() == ChattingRoomType.MANY_TO_MANY.getValue()) {
                sendEventToRoom(internalIdx, messageEventOld);

            } else if (room.getChattingRoomType() == ChattingRoomType.ONE_TO_MANY.getValue()) {
                user = chattingUsers.get(internalIdx);
                if (user != null && user.isAdmin()) {
                    sendEventToRoom(internalIdx, messageEventOld);
                } else {
                    sendEventToPerson(room.getChattingRoomSeq(), room.getAdminIdx(), messageEventOld);
                    sendEventToPerson(room.getChattingRoomSeq(), messageEventOld.getFromUserIdx(), messageEventOld);
                }

            } else if (room.getChattingRoomType() == ChattingRoomType.APPROVAL.getValue()) {
                user = chattingUsers.get(internalIdx);
                if (user != null && user.isAdmin()) {
                    // admin user : without approval
                    sendEventToRoom(internalIdx, messageEventOld);
                } else {
                    // normal user : send approval request to admin
                    messageEventOld.setMessageEventType(MessageEventType.REQ_APPROVAL_MSG.getValue());
                    sendEventToPerson(room.getChattingRoomSeq(), room.getAdminIdx(), messageEventOld);
                    MessageEvent waitMessageEventOld = EventManager.cloneEvent(messageEventOld);
                    waitMessageEventOld.setMessageEventType(MessageEventType.WAIT_APPROVAL_MSG.getValue());
                    sendEventToPerson(waitMessageEventOld.getProgramIdx(), waitMessageEventOld.getFromUserIdx(), waitMessageEventOld);
                }
            }
        } else {
            throw new BadArgumentException();
        }
    }

    public void checkUsersTimeout() {
        Iterator<Map.Entry<Long, ChattingUserData>> iter = chattingUsers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, ChattingUserData> userEntry = iter.next();
            ChattingUserData user = userEntry.getValue();
            if (user != null) {
                if (user.checkTimeOut()) {
                    try {
                        leaveChatRoom(user.getInternalIdx(), user.getProgramIdx(), iter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
