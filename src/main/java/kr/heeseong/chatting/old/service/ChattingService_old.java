package kr.heeseong.chatting.old.service;

import kr.heeseong.chatting.old.event_enum.ChattingRoomType;
import kr.heeseong.chatting.old.event_enum.MessageEventType;
import kr.heeseong.chatting.old.exceptions.*;
import kr.heeseong.chatting.old.mapper.ChattingMapper;
import kr.heeseong.chatting.old.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class ChattingService_old {

    private ConcurrentHashMap<Integer, ChattingRoomDataOld> chattingRooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ChattingUserDataOld> chattingUsers = new ConcurrentHashMap<>();
    private Object chattingRoomLock = new Object();
    private Object chattingUserLock = new Object();
    private long internalIndex = 0;

    private final ChattingMapper chattingMapper;

    @Autowired
    private ChattingService_old(ChattingMapper chattingMapper) {
        this.chattingMapper = chattingMapper;
    }

    public ChattingRoomOld enterChattingRoom(ChattingRoomOld chattingRoom) throws Exception {
        ChattingUsersOld chattingUsersOld = new ChattingUsersOld(chattingRoom.getUserIdx(), chattingRoom.getUserId(), chattingRoom.getUserName(), chattingRoom.isAdmin());
        ChattingRoomOld resultChattingRoom = this.enterChattingRoom(chattingRoom, chattingUsersOld, true);

        MessageEventOld roomMessageEventOld = new MessageEventOld(
                MessageEventType.ENTER_USER.getValue()
                , resultChattingRoom.getProgramIdx()
                , chattingUsersOld.getUserIdx()
                , chattingUsersOld.getUserIdx()
                , chattingUsersOld.getUserName()
                , ""
                , chattingUsersOld.getUserId());

        chattingMapper.insertEvent(roomMessageEventOld);
        return resultChattingRoom;
    }


    public ArrayList<ChattingRoomOld> listChatRooms() {
        ArrayList<ChattingRoomOld> roomList = new ArrayList<ChattingRoomOld>();

        for (Map.Entry<Integer, ChattingRoomDataOld> roomEntry : chattingRooms.entrySet()) {
            ChattingRoomDataOld room = roomEntry.getValue();
            if (room != null) {
                roomList.add(room.getChattingRoom());
            }
        }

        return roomList;
    }

    public ChattingRoomOld getChatRoom(int roomIdx) {
        ChattingRoomDataOld room = chattingRooms.get(roomIdx);
        if (room != null) {
            return room.getChattingRoom();
        }
        return null;
    }

    public ArrayList<ChattingUsersOld> listUsers(int roomIdx) {
        ArrayList<ChattingUsersOld> userList = new ArrayList<ChattingUsersOld>();

        ChattingRoomDataOld chattingRoomData = chattingRooms.get(roomIdx);
        if (chattingRoomData == null) {
            return null;
        }

        for (Map.Entry<Long, ChattingUsersOld> userEntry : chattingRoomData.getUserList().entrySet()) {
            userList.add(userEntry.getValue());
        }

        return userList;
    }

    public void leaveChatRoom(int programIdx, int userIdx, long internalIdx) throws Exception {
        this.goleaveChatRoom(internalIdx, programIdx, null);
        MessageEventOld roomMessageEventOld = new MessageEventOld(MessageEventType.LEAVE_USER.getValue(), programIdx, userIdx, 0, "", "", "");
        chattingMapper.insertEvent(roomMessageEventOld);
    }


    public void addBlackList(long internalIdx, int userIdx, int programIdx, long blackUserIdx) throws Exception {
        //TODO 메모리에 담는 구조임 현재, 디비에도 담고 꺼낼 수 있도록 개선 해야함
        this.addBlackList(internalIdx, programIdx, blackUserIdx);

        MessageEventOld roomMessageEventOld = new MessageEventOld(MessageEventType.ADD_BLACKLIST.getValue(), programIdx, blackUserIdx, userIdx, "", "", "");
        chattingMapper.insertEvent(roomMessageEventOld);
    }

    public void removeBlackList(long internalIdx, int userIdx, int programIdx, long blackUserIdx) throws Exception {
        this.removeBlackList(internalIdx, programIdx, blackUserIdx);
        MessageEventOld messageEventOld = new MessageEventOld(MessageEventType.REMOVE_BLACKLIST.getValue(), programIdx, blackUserIdx, userIdx, "", "", "");
        chattingMapper.insertEvent(messageEventOld);
    }

    public MessageEventOld sendEvent(long internalIdx, MessageEventOld messageEventOld) throws Exception {
        chattingMapper.insertEvent(messageEventOld);
        messageEventOld.setIdx(messageEventOld.getIdx());

        this.sendMessageEvent(internalIdx, messageEventOld);

        return messageEventOld;
    }

    public ArrayList<MessageEventOld> getNewEvents(long internalIdx) throws Exception {
        ChattingUserDataOld user = chattingUsers.get(internalIdx);
        if (user == null) {
            throw new UserNotExistException();
        }

        return user.getEvents();
    }

    private ChattingRoomOld enterChattingRoom(ChattingRoomOld chattingRoom, ChattingUsersOld chattingUsersOld, boolean notify) throws Exception {
        if (chattingUsersOld.getInternalIdx() != -1) {
            throw new BadArgumentException();
        }

        ChattingUserDataOld chattingUserDataOld = setChattingUser(chattingUsersOld);
        ChattingRoomDataOld chattingRoomData = chattingRooms.get(chattingRoom.getProgramIdx());
        if (chattingRoomData == null) {
            chattingRoomData = createChattingRoom(chattingRoom, true);
        }

        if (chattingRoomData.addUser(chattingUserDataOld.getChattingUsersOld()) == -1) {
            throw new UserExistException();
        }

        chattingUserDataOld.setProgramIdx(chattingRoom.getProgramIdx());
        if (notify) {
            MessageEventOld messageEventOld = EventManagerOld.makeEnterRoomEvent(chattingRoom.getProgramIdx(), chattingUsersOld);
            sendMessageEvent(chattingUserDataOld.getInternalIdx(), messageEventOld);
            chattingMapper.insertEvent(messageEventOld);
        }

        chattingRoom.setInternalIdx(chattingUserDataOld.getInternalIdx());
        return chattingRoom;
    }

    public int goleaveChatRoom(long internalIdx, int roomIdx, Iterator<Map.Entry<Long, ChattingUserDataOld>> userIteration) throws Exception {
        if (roomIdx != -1) {
            ChattingRoomDataOld chatRoomManager = chattingRooms.get(roomIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeUser(internalIdx);

            ChattingUserDataOld user = chattingUsers.get(internalIdx);
            if (user != null) {
                removeUser(internalIdx, userIteration);
            }

            if (chatRoomManager.getInternalUsers().size() == 0) {
                removeChatRoom(internalIdx, roomIdx);
            } else {
                MessageEventOld messageEventOld = EventManagerOld.makeLeaveRoomEvent(roomIdx, user.getUserIdx());
                sendMessageEvent(internalIdx, messageEventOld);
                chattingMapper.insertEvent(messageEventOld);
            }
        } else {
            throw new BadArgumentException();
        }

        return 0;
    }

    public Long[] getBlackList(long internalIdx, int roomIdx) throws Exception {
        checkAdmin(internalIdx);

        ChattingRoomDataOld chatRoomManager = chattingRooms.get(roomIdx);
        if (chatRoomManager == null) {
            return null;
        }

        return chatRoomManager.getBlackListArray();
    }

    private void addBlackList(long internalIdx, int programIdx, long blackUser) throws Exception {
        checkAdmin(internalIdx);

        if (programIdx != -1) {
            ChattingRoomDataOld chatRoomManager = chattingRooms.get(programIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }
            chatRoomManager.addBlackList(blackUser);
        } else {
            throw new BadArgumentException();
        }
    }

    private void removeBlackList(long internalIdx, int programIdx, long blackUser) throws Exception {
        checkAdmin(internalIdx);

        if (programIdx != -1) {
            ChattingRoomDataOld chatRoomManager = chattingRooms.get(programIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeBlackList(blackUser);
        }
    }

    private void sendMessageEvent(long internalIdx, MessageEventOld messageEventOld) throws Exception {
        if (messageEventOld.getMessageEventType() == MessageEventType.NORMAL_MSG.getValue()) {
            //sendMessage(internalIdx, messageEventOld);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.ENTER_USER.getValue()) {
            sendEventToRoom(internalIdx, messageEventOld, false);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.LEAVE_USER.getValue()) {
            sendEventToRoom(internalIdx, messageEventOld, false);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.APPROVED_MSG.getValue()) {
            sendEventToPerson(messageEventOld.getProgramIdx(), messageEventOld.getFromUserIdx(), messageEventOld);
            MessageEventOld newMessageEventOld = EventManagerOld.cloneEvent(messageEventOld);
            newMessageEventOld.setMessageEventType(MessageEventType.NORMAL_MSG.getValue());
            sendEventToRoom(internalIdx, newMessageEventOld);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.REJECTED_MSG.getValue()) {
            sendEventToPerson(messageEventOld.getProgramIdx(), messageEventOld.getFromUserIdx(), messageEventOld);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.DIRECT_MSG.getValue()) {
            sendEventToPerson(messageEventOld.getProgramIdx(), messageEventOld.getToUserIdx(), messageEventOld);
            sendEventToPerson(internalIdx, messageEventOld);

        } else if (messageEventOld.getMessageEventType() == MessageEventType.ADMIN_MSG.getValue()) {
            sendEventToRoom(internalIdx, messageEventOld, true);

        } else {
            throw new Exception();
        }

		/*switch (event.getType()) {
		case EventType.CREATE_CHATROOM:
			sendEventToAll(internalIdx, event);
			break;
		case EventType.REMOVE_CHATROOM:
			sendEventToAll(internalIdx, event);
			break;
		}*/
    }

    public ChattingUserDataOld setChattingUser(ChattingUsersOld chattingUsersOld) {
        internalIndex++;
        chattingUsersOld.setInternalIdx(internalIndex);

        //자주 사용 되는 객체 이기 때문에 약한 참조 처리
        WeakReference<ChattingUserDataOld> userRef = new WeakReference<>(new ChattingUserDataOld(chattingUsersOld));
        ChattingUserDataOld chattingUserDataOld = userRef.get();

        synchronized (chattingUserLock) {
            this.chattingUsers.put(internalIndex, chattingUserDataOld);
        }

        return chattingUserDataOld;
    }

    public int removeUser(long internalIdx, Iterator<Map.Entry<Long, ChattingUserDataOld>> userIteration) throws Exception {
        ChattingUserDataOld user = chattingUsers.get(internalIdx);
        if (user == null) {
            return -1;
        }

        user.removeAll();

        synchronized (chattingUserLock) {
            if (userIteration != null) {
                userIteration.remove();
            } else {
                chattingUsers.remove(internalIdx);
            }
            user = null;
        }

        return 0;
    }


    public int leaveChatRoom(long internalIdx, int roomIdx, Iterator<Map.Entry<Long, ChattingUserDataOld>> userIteration) throws Exception {
        if (roomIdx != -1) {
            ChattingRoomDataOld chatRoomManager = chattingRooms.get(roomIdx);
            if (chatRoomManager == null) {
                throw new ChatRoomNotExistException();
            }

            chatRoomManager.removeUser(internalIdx);

            ChattingUserDataOld user = chattingUsers.get(internalIdx);
            if (user != null) {
                this.removeUser(internalIdx, userIteration);
            }

            if (chatRoomManager.getInternalUsers().size() == 0) {
                this.removeChatRoom(internalIdx, roomIdx);
            } else {
                MessageEventOld messageEventOld = EventManagerOld.makeLeaveRoomEvent(roomIdx, user.getUserIdx());
                this.sendEvent(internalIdx, messageEventOld);
                chattingMapper.insertEvent(messageEventOld);
            }
        } else {
            throw new BadArgumentException();
        }

        return 0;
    }

    private void removeChatRoom(long internalIdx, int roomIdx) throws Exception {
        ChattingRoomDataOld chatRoomManager = chattingRooms.get(roomIdx);
        if (chatRoomManager == null) {
            throw new ChatRoomNotExistException();
        }

        synchronized (chattingRoomLock) {
            chattingRooms.remove(roomIdx);
            chatRoomManager = null;
        }

        MessageEventOld messageEventOld = EventManagerOld.removeChatRoomEvent(roomIdx);
        sendEvent(internalIdx, messageEventOld);
        chattingMapper.insertEvent(messageEventOld);
    }

    private void checkAdmin(long internalIdx) throws Exception {
        ChattingUserDataOld user = chattingUsers.get(internalIdx);
        if (user == null || user.isAdmin() != true) {
            throw new UnauthorizedException();
        }
    }

    public ChattingRoomDataOld createChattingRoom(ChattingRoomOld chattingRoom, boolean log) throws Exception {
        ChattingRoomDataOld chattingRoomData;

        synchronized (chattingRoomLock) {
            if (chattingRooms.get(chattingRoom.getProgramIdx()) != null) {
                throw new ChatRoomExistException();
            }

            WeakReference<ChattingRoomDataOld> chatRoomRef = new WeakReference<ChattingRoomDataOld>(new ChattingRoomDataOld());
            chattingRoomData = chatRoomRef.get();
            chattingRoomData.setChattingRoom(chattingRoom);
            chattingRooms.put(chattingRoomData.getProgramIdx(), chattingRoomData);
        }

        if (log) {
            MessageEventOld messageEventOld = EventManagerOld.makeCreateRoomEvent(chattingRoom);
            //sendEvent(internalIdx, event);
            chattingMapper.insertEvent(messageEventOld);
        }

        return chattingRoomData;
    }

    private void sendEventToPerson(int roomIdx, long userIdx, MessageEventOld messageEventOld) {
        ChattingRoomDataOld room = chattingRooms.get(roomIdx);
        if (room != null) {
            for (Long keyIndex : room.getInternalUsers()) {
                ChattingUserDataOld user = chattingUsers.get(keyIndex);
                if (userIdx == user.getUserIdx()) {
                    sendEventToPerson(keyIndex, messageEventOld);
                }
            }
        }
    }

    private void sendEventToRoom(long internalIdx, MessageEventOld messageEventOld, boolean sendMyself) {
        ChattingRoomDataOld room = chattingRooms.get(messageEventOld.getProgramIdx());
        if (room != null) {
            for (Long keyIndex : room.getInternalUsers()) {
                if (sendMyself == true || (sendMyself == false && internalIdx != keyIndex)) {
                    ChattingUserDataOld user = chattingUsers.get(keyIndex);
                    if (user != null) {
                        user.postMessage(messageEventOld);
                    }
                }
            }
        }
    }

    private void sendEventToRoom(long internalIdx, MessageEventOld messageEventOld) {
        sendEventToRoom(internalIdx, messageEventOld, true);
    }

    private void sendEventToPerson(long internalIdx, MessageEventOld messageEventOld) {
        ChattingUserDataOld user = chattingUsers.get(internalIdx);
        if (user != null) {
            try {
                //메시지 담는 구간
                user.postMessage(messageEventOld);
            } catch (Exception e) {
                if (user.checkTimeout()) {
                    try {
                        leaveChatRoom(internalIdx, user.getProgramIdx(), null);
                    } catch (Exception ex) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


//    public void sendMessage(long internalIdx, MessageEventOld messageEventOld) throws Exception {
//
//        ChattingRoomDataOld room = chattingRooms.get(messageEventOld.getProgramIdx());
//        if (room != null) {
//            ChattingUserDataOld user;
//
//            if (room.isBlackList(messageEventOld.getFromUserIdx())) {
//                messageEventOld.setMessageEventType(MessageEventType.BLOCKED_MSG.getValue());
//                sendEventToPerson(internalIdx, messageEventOld);
//                return;
//            }
//
//            if (room.getChattingRoomType() == ChattingRoomType.MANY_TO_MANY.getValue()) {
//                sendEventToRoom(internalIdx, messageEventOld);
//
//            } else if (room.getChattingRoomType() == ChattingRoomType.ONE_TO_MANY.getValue()) {
//                user = chattingUsers.get(internalIdx);
//                if (user != null && user.isAdmin()) {
//                    sendEventToRoom(internalIdx, messageEventOld);
//                } else {
//                    sendEventToPerson(room.getProgramIdx(), room.getAdminIdx(), messageEventOld);
//                    sendEventToPerson(room.getProgramIdx(), messageEventOld.getFromUserIdx(), messageEventOld);
//                }
//
//            } else if (room.getChattingRoomType() == ChattingRoomType.APPROVAL.getValue()) {
//                user = chattingUsers.get(internalIdx);
//                if (user != null && user.isAdmin()) {
//                    // admin user : without approval
//                    sendEventToRoom(internalIdx, messageEventOld);
//                } else {
//                    // normal user : send approval request to admin
//                    messageEventOld.setMessageEventType(MessageEventType.REQ_APPROVAL_MSG.getValue());
//                    sendEventToPerson(room.getProgramIdx(), room.getAdminIdx(), messageEventOld);
//                    MessageEventOld waitMessageEventOld = EventManagerOld.cloneEvent(messageEventOld);
//                    waitMessageEventOld.setMessageEventType(MessageEventType.WAIT_APPROVAL_MSG.getValue());
//                    sendEventToPerson(waitMessageEventOld.getProgramIdx(), waitMessageEventOld.getFromUserIdx(), waitMessageEventOld);
//                }
//            }
//        } else {
//            throw new BadArgumentException();
//        }
//    }

    public void checkUsersTimeout() {
        Iterator<Map.Entry<Long, ChattingUserDataOld>> iter = chattingUsers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, ChattingUserDataOld> userEntry = iter.next();
            ChattingUserDataOld user = userEntry.getValue();
            if (user != null) {
                if (user.checkTimeout()) {
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
