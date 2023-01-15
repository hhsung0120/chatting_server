package kr.heeseong.chatting.room.model;

import kr.heeseong.chatting.old.exceptions.BadArgumentException;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class ChattingRoom extends ChattingUser {

    private Long chattingRoomSeq;
    private String name;
    private String description;
    private int roomType;
    private long adminIdx;
    private int categorySeq;
    private String roomTitle;
    private String password;
    private String secretModeUseYn;
    private String simultaneousConnectionsUseYn;
    private String useYn;

    public ChattingUser getChattingUser() {
        Boolean isAdmin = "admin".equals(super.getUserId());
        return new ChattingUser(super.getUserIdx(), super.getUserId(), super.getUserName(), isAdmin);
    }

    public ChattingRoom() {
    }

    @Builder(builderClassName = "createRoomBuilder", builderMethodName = "createRoomBuilder")
    public ChattingRoom(Map<String, String> data) throws BadArgumentException {
        super(Long.valueOf(data.get("userIdx")));

        try{

        } catch (Exception e) {
            throw e;
        }
    }
}
