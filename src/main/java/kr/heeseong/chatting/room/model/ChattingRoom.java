package kr.heeseong.chatting.room.model;

import kr.heeseong.chatting.eventenum.ChattingRoomType;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.thymeleaf.util.StringUtils;

import java.util.Map;

@Log4j2
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChattingRoom extends ChattingUser {

    private Long chattingRoomSeq;
    private ChattingRoomType roomType;
    private String name;
    private String description;
    private Long adminIdx;
    private int categorySeq;
    private String roomTitle;
    private String password;
    private String secretModeUseYn;
    private String simultaneousConnectionsUseYn;
    private String useYn;

    public ChattingUser getChattingUser() {
        return new ChattingUser(super.getUserIdx(), super.getUserId(), super.getUserName());
    }

    public static ChattingRoom setCreateRoom(Map<String, String> createRoomData) {
        return new ChattingRoom(createRoomData);
    }

    private ChattingRoom(Map<String, String> createRoomData) {
        //TODO : 후에 세션이나 JWT 정보로 대체
        super(Long.valueOf(createRoomData.get("userIdx")), createRoomData.get("userId"), createRoomData.get("userName"));

        adminIdx = Long.valueOf(createRoomData.get("userIdx"));

        //TODO : 이 값은 추후 DB 값으로 대체 해야함
        try {
            chattingRoomSeq = Long.valueOf(createRoomData.get("chattingRoomSeq"));
            if (StringUtils.isEmpty(String.valueOf(chattingRoomSeq))) {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("chattingRoomSeq value is required : {}", chattingRoomSeq);
            throw new IllegalArgumentException();
        }

        try {
            roomType = ChattingRoomType.valueOf(createRoomData.get("roomType"));
            if (StringUtils.isEmpty(String.valueOf(roomType))) {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("invalid room type : {}", createRoomData.get("roomType"));
            throw new IllegalArgumentException();
        }

        name = createRoomData.get("name");
        if (StringUtils.isEmpty(name)) {
            log.error("name value is required : {}", createRoomData.get("name"));
            throw new IllegalArgumentException();
        }

        categorySeq = Integer.parseInt(createRoomData.get("categorySeq"));
        if (StringUtils.isEmpty(String.valueOf(categorySeq))) {
            log.error("roomTitle value is required : {}", createRoomData.get("roomTitle"));
            throw new IllegalArgumentException();
        }

        roomTitle = createRoomData.get("roomTitle");
        if (StringUtils.isEmpty(roomTitle)) {
            log.error("roomTitle value is required : {}", createRoomData.get("roomTitle"));
            throw new IllegalArgumentException();
        }

        secretModeUseYn = createRoomData.get("secretModeUseYn");
        if (StringUtils.isEmpty(secretModeUseYn) || (!"n".equalsIgnoreCase(secretModeUseYn) && !"y".equalsIgnoreCase(secretModeUseYn))) {
            log.error("invalid secretModeUseYn value : {}", createRoomData.get("secretModeUseYn"));
            throw new IllegalArgumentException();
        }

        simultaneousConnectionsUseYn = createRoomData.get("simultaneousConnectionsUseYn");
        if (StringUtils.isEmpty(simultaneousConnectionsUseYn) || (!"n".equalsIgnoreCase(simultaneousConnectionsUseYn) && !"y".equalsIgnoreCase(simultaneousConnectionsUseYn))) {
            log.error("invalid simultaneousConnectionsUseYn value : {}", createRoomData.get("simultaneousConnectionsUseYn"));
            throw new IllegalArgumentException();
        }

        description = createRoomData.get("description");
        if (StringUtils.isEmpty(description)) {
            description = "";
        }

        password = createRoomData.get("password");
        if (StringUtils.isEmpty(password)) {
            password = "";
        }
    }
}
