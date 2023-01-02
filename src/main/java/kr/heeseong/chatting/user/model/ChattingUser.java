package kr.heeseong.chatting.user.model;

import lombok.Data;

@Data
public class ChattingUser {

    private long internalIdx;
    private long userIdx;
    private String userId;
    private String userName;
    private boolean isAdmin;

    public ChattingUser() {
    }

    public ChattingUser(long userIdx, String userId, String userName, boolean isAdmin) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = isAdmin;
        this.internalIdx = -1;
    }
}
