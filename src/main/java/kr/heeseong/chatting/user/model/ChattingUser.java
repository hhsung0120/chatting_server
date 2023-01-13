package kr.heeseong.chatting.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChattingUser {

    @Setter
    private Long internalIdx;
    private Long userIdx;
    private String userId;
    private String userName;
    private boolean isAdmin;

    private Long chattingRoomSeq;

    public ChattingUser() {
    }

    public ChattingUser(Long chattingRoomSeq, Long userIdx) {
        this.chattingRoomSeq = chattingRoomSeq;
        this.userIdx = userIdx;
    }

    public ChattingUser(Long userIdx, String userId, String userName, boolean isAdmin, Long internalIdx) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = isAdmin;
        this.internalIdx = internalIdx;
    }
}
