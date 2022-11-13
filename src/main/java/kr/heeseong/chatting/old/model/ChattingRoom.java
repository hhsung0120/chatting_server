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

    //신규
    private int categorySeq;
    private String roomTitle;
    private String password;
    private String secretModeUseYn;
    private String simultaneousConnectionsUseYn;
    private String useYn;

    public ChattingRoom() {
    }

    public ChattingRoom(String name, String description, String password, String status, int type, long userIdx, long adminIdx, int programIdx) {
        this.name = name;
        this.description = description;
        this.password = password;
        this.status = status;
        this.roomType = type;
        this.adminIdx = adminIdx;
        this.programIdx = programIdx;
        this.setUserIdx(userIdx);
    }
}
