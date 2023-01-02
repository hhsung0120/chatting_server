package kr.heeseong.chatting.old.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ChattingRoomOld extends ChattingUsersOld {

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

    public ChattingUsersOld getChattingUser(){
        Boolean isAdmin = "admin".equals(super.getUserId());
        return new ChattingUsersOld(super.getUserIdx(), super.getUserId(), super.getUserName(), isAdmin);
    }
}
