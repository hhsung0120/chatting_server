package kr.heeseong.chatting.old.model;

import lombok.Data;

@Data
public class ChattingUsers {

    private long internalIdx;
    private long userIdx;
    private String userId;
    private String userName;
    private boolean isAdmin;

    public ChattingUsers() {
    }

    public ChattingUsers(long userIdx, String userId, String userName, boolean isAdmin) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = isAdmin;
        this.internalIdx = -1;
    }
}