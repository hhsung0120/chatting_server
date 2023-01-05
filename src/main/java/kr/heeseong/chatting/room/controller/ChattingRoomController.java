package kr.heeseong.chatting.room.controller;

import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting-room")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;

    @PostMapping("/enter-user")
    public ChattingRoom enterChatRoom(
            @RequestBody ChattingRoom ChattingRoom) throws Exception {

        log.info("enterChatRoom : {}", ChattingRoom);
        log.info("enterChatRoomUser : {}", ChattingRoom.getChattingUser());
        return chattingRoomService.enterChattingRoom(ChattingRoom);
    }
}
