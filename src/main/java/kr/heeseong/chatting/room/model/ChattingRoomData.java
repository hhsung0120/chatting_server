package kr.heeseong.chatting.room.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.heeseong.chatting.eventenum.ChattingRoomType;
import kr.heeseong.chatting.exceptions.BadArgumentException;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChattingRoomData {

    @Setter
    private ChattingRoom chattingRoom;
    private ConcurrentHashMap<Long, ChattingUser> users;
    private Object userLock = new Object();
    private Object blackLock = new Object();
    private HashSet<Long> blockList = new HashSet<>();

    @JsonIgnore
    public String getPassword() {
        if (chattingRoom != null) {
            return chattingRoom.getPassword();
        }
        return null;
    }

    @JsonIgnore
    public String getDescription() {
        if (chattingRoom != null) {
            return chattingRoom.getDescription();
        }
        return null;
    }

    @JsonIgnore
    public Long getChattingRoomSeq() {
        if (chattingRoom != null) {
            return chattingRoom.getChattingRoomSeq();
        }
        return 0L;
    }

    @JsonIgnore
    public long getUserIdx() {
        if (chattingRoom != null) {
            return chattingRoom.getUserIdx();
        }
        return -1;
    }

//	@JsonIgnore
//	public String getStatus() {
//		if (chattingRoom != null) {
//			return chattingRoom.getStatus();
//		}
//		return null;
//	}

    @JsonIgnore
    public ChattingRoomType getChattingRoomType() throws BadArgumentException {
        if (chattingRoom != null) {
            return chattingRoom.getRoomType();
        }

        throw new BadArgumentException();
    }

    @JsonIgnore
    public long getAdminIdx() {
        if (chattingRoom != null) {
            return chattingRoom.getAdminIdx();
        }
        return 0;
    }

    @JsonIgnore
    public ConcurrentHashMap<Long, ChattingUser> getUserList() {
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
        return users;
    }

    public Set<Long> getUsers() {
        Set<Long> userIdxs = new HashSet<Long>();

        for (Long keyIndex : getInternalUsers()) {
            ChattingUser user = users.get(keyIndex);
            userIdxs.add(user.getUserIdx());
        }
        return userIdxs;
    }

    public Set<Long> getInternalUsers() {
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }

        return users.keySet();
    }

    public boolean addUser(ChattingUser user) {
        if (getInternalUsers().contains(user.getUserIdx())) {
            return false;
        }

        users.put(user.getUserIdx(), user);
        return true;
    }

    public int removeUser(Long internalIdx) {
        if (getInternalUsers().contains(internalIdx) == false) {
            return -1;
        }

        synchronized (userLock) {
            users.remove(internalIdx);
        }

        return 0;
    }

    public void addBlockList(Long userIdx) {
        synchronized (blackLock) {
            blockList.add(userIdx);
        }
    }

    public boolean isBlockUser(Long userIdx) {
        return blockList.contains(userIdx);
    }

    public HashSet<Long> getBlackList() {
        return blockList;
    }

    @JsonIgnore
    public Long[] getBlackListArray() {
        return (Long[]) blockList.toArray();
    }

    public void removeBlockList(Long userIdx) {
        synchronized (blackLock) {
            blockList.remove(userIdx);
        }
    }
}
