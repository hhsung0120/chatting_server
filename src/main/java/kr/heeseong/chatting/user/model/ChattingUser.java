package kr.heeseong.chatting.user.model;

import lombok.Getter;

@Getter
public class ChattingUser {

    private Long userIdx;
    private String userId;
    private String userName;
    private boolean isAdmin;
    private Long chattingRoomSeq;

    public ChattingUser() {

    }

    public ChattingUser(Long userIdx) {
        this.userIdx = userIdx;
    }

    public ChattingUser(Long userIdx, String userId, String userName) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = "admin".equals(userId);
    }

    public ChattingUser(Long userIdx, String userId, String userName, boolean isAdmin) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = isAdmin;
    }

    public String toStringUser() {
        return "ChattingUser{" +
                "userIdx=" + userIdx +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", isAdmin=" + isAdmin +
                ", chattingRoomSeq=" + chattingRoomSeq +
                '}';
    }
}
