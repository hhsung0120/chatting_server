package kr.heeseong.chatting.room.controller;

import kr.heeseong.chatting.old.model.ChattingRoom;
import kr.heeseong.chatting.old.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting-room")
public class ChattingRoomController {

    private final ChattingService chattingService;

    @PostMapping("/user-enter")
    public ChattingRoom enterChatRoom(
            @RequestHeader("userIdx") long userIdx
            , @RequestHeader("userId") String userId
            , @RequestHeader("userName") String userName
            , @RequestHeader("isAdmin") boolean isAdmin
            , @RequestBody ChattingRoom chattingRoom) throws Exception {

        System.out.println(userIdx);
        System.out.println(userId);
        System.out.println(userName);
        System.out.println(isAdmin);
        System.out.println(chattingRoom.toString());

        return chattingService.enterChattingRoom(chattingRoom);
    }
}
