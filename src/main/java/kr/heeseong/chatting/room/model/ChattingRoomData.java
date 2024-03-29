package kr.heeseong.chatting.room.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.heeseong.chatting.eventenum.ChattingRoomType;
import kr.heeseong.chatting.exceptions.BadArgumentException;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ToString
public class ChattingRoomData {

    @Setter
    private ChattingRoom chattingRoom;
    private ConcurrentHashMap<Long, ChattingUser> users;
    private HashSet<Long> blockList = new HashSet<>();

    @JsonIgnore
    public ChattingRoomType getChattingRoomType() throws BadArgumentException {
        if (chattingRoom != null) {
            return chattingRoom.getRoomType();
        }
        throw new BadArgumentException();
    }

    @JsonIgnore
    public Long getAdminIdx() {
        if (chattingRoom != null) {
            return chattingRoom.getAdminIdx();
        }
        return null;
    }

    @JsonIgnore
    public ConcurrentHashMap<Long, ChattingUser> getUserList() {
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
        return users;
    }

    @JsonIgnore
    public Set<Long> getUsers() {
        Set<Long> userIdxs = new HashSet<Long>();

        for (Long keyIndex : getInternalUsers()) {
            ChattingUser user = users.get(keyIndex);
            userIdxs.add(user.getUserIdx());
        }
        return userIdxs;
    }

    @JsonIgnore
    public Set<Long> getInternalUsers() {
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
        return users.keySet();
    }

    @JsonIgnore
    public boolean addUser(ChattingUser user) {
        if (getInternalUsers().contains(user.getUserIdx())) {
            return false;
        }

        users.put(user.getUserIdx(), user);
        return true;
    }

    @JsonIgnore
    public int removeUser(Long internalIdx) {
        if (getInternalUsers().contains(internalIdx) == false) {
            return -1;
        }

        users.remove(internalIdx);
        return 0;
    }

    @JsonIgnore
    public void addBlockList(Long userIdx) {
        blockList.add(userIdx);
    }

    @JsonIgnore
    public boolean isBlockUser(Long userIdx) {
        return blockList.contains(userIdx);
    }

    @JsonIgnore
    public HashSet<Long> getBlackList() {
        return blockList;
    }

    @JsonIgnore
    public Long[] getBlackListArray() {
        return (Long[]) blockList.toArray();
    }

    @JsonIgnore
    public void removeBlockList(Long userIdx) {
        blockList.remove(userIdx);
    }
}
