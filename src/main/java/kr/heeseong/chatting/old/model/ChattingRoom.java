package kr.heeseong.chatting.old.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ChattingRoom extends ChattingUsers {

    //올드 영역
    @Setter
    private long internalIdx;
    private int programIdx;
    private String name;
    private String description;
    private int roomType; //
    private long adminIdx;
    private String status;

    //리팩토링 영역
    private int categorySeq;
    private String roomTitle;
    private String password;
    private String secretModeUseYn;
    private String simultaneousConnectionsUseYn;
    private String useYn;

    public ChattingUsers getChattingUser(){
        Boolean isAdmin = "admin".equals(super.getUserId());
        return new ChattingUsers(super.getUserIdx(), super.getUserId(), super.getUserName(), isAdmin);
    }
}
