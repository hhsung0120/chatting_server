package kr.heeseong.chatting.user.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChattingUser {

    private Long userIdx;
    private String userId;
    private String userName;
    private boolean isAdmin;

    public ChattingUser() {
    }

    public ChattingUser(Long userIdx, String userId, String userName) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userName = userName;
        this.isAdmin = "admin".equals(userId);
    }
}
