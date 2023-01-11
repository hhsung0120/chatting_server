package kr.heeseong.chatting.room.model;

import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ChattingRoom extends ChattingUser {

    //올드 영역
    @Setter
    private Long internalIdx;
    private int programIdx;
    private String name;
    private String description;
    private int roomType; //
    private long adminIdx;

    //리팩토링 영역
    private Long chattingRoomSeq;
    private int categorySeq;
    private String roomTitle;
    private String password;
    private String secretModeUseYn;
    private String simultaneousConnectionsUseYn;
    private String useYn;

    public ChattingUser getChattingUser() {
        Boolean isAdmin = "admin".equals(super.getUserId());
        return new ChattingUser(super.getUserIdx(), super.getUserId(), super.getUserName(), isAdmin, internalIdx);
    }

    public Long getInternalIdx() {
        return getUserIdx();
    }
}
