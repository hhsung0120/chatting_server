package kr.heeseong.chatting.old.controller;

import kr.heeseong.chatting.old.model.ChattingRoomOld;
import kr.heeseong.chatting.old.model.ChattingUsersOld;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Log4j2
@RestController
@RequestMapping("/chattingRoom")
public class ChattingRoomControllerOld {

    private final ChattingRoomService chattingService;

    @Autowired
    public ChattingRoomControllerOld(ChattingRoomService chattingService) {
        this.chattingService = chattingService;
    }

    @PostMapping("/enterUser")
    public ChattingRoom enterChatRoom(
            @RequestHeader("userIdx") long userIdx
            , @RequestHeader("userId") String userId
            , @RequestHeader("userName") String userName
            , @RequestHeader("isAdmin") boolean isAdmin
            , @RequestBody ChattingRoom chattingRoom) throws Exception {

        log.info("old parameter1 {}", chattingRoom);

        chattingRoom.setUserIdx(userIdx);
        chattingRoom.setUserId(userId);
        chattingRoom.setUserName(userName);
        chattingRoom.setAdmin(isAdmin);

        log.info("old parameter2 {}", chattingRoom);
        log.info("old parameter2 {}", chattingRoom.getUserId());
        log.info("old parameter2 {}", chattingRoom.getUserIdx());
        log.info("old parameter2 {}", chattingRoom.getUserName());

        return chattingService.enterChattingRoom(chattingRoom);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ArrayList<ChattingUser> listUsers(
            @RequestParam("programIdx") int programIdx) {
        return chattingService.listUsers(programIdx);
    }

    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public void leaveChatRoom(
            @RequestHeader("internalIdx") long internalIdx
            , @RequestHeader("programIdx") int programIdx
            , @RequestHeader("userIdx") int userIdx) throws Exception {
        chattingService.leaveChatRoom(programIdx, userIdx, internalIdx);
    }

    @RequestMapping(value = "/blacklist", method = RequestMethod.GET)
    public Long[] getBlackList(
            @RequestHeader("internalIdx") long internalIdx,
            @RequestHeader("programIdx") int programIdx) throws Exception {
        return chattingService.getBlackList(internalIdx, programIdx);
    }

    @RequestMapping(value = "/blacklist", method = RequestMethod.POST)
    public void addBlackList(
            @RequestHeader("internalIdx") long internalIdx,
            @RequestHeader("programIdx") int programIdx,
            @RequestHeader("userIdx") int userIdx,
            @RequestBody ChattingUsersOld blackUser) throws Exception {
        chattingService.addBlackList(internalIdx, userIdx, programIdx, blackUser.getUserIdx());
    }

    @RequestMapping(value = "/blacklist", method = RequestMethod.DELETE)
    public void removeBlackList(
            @RequestHeader("internalIdx") long internalIdx,
            @RequestHeader("programIdx") int programIdx,
            @RequestHeader("userIdx") int userIdx,
            @RequestBody ChattingUsersOld blackUser) throws Exception {
        chattingService.removeBlackList(internalIdx, userIdx, programIdx, blackUser.getUserIdx());
    }


}
