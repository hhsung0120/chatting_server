package kr.heeseong.chatting.room.controller;

import kr.heeseong.chatting.event.service.EventService;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.user.model.ChattingUser;
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

    private final EventService eventService;

    @PostMapping("/create")
    public ChattingRoom createChattingRoom(@RequestBody ChattingRoom ChattingRoom) throws Exception {
        log.info("createChattingRoom : {}", ChattingRoom);
        return eventService.enterChattingRoom(ChattingRoom);
    }

    @PostMapping("/enter-user")
    public ChattingRoom enterChatRoom(@RequestBody ChattingRoom ChattingRoom) throws Exception {
        log.info("enterChatRoom : {}", ChattingRoom);
        return eventService.enterChattingRoom(ChattingRoom);
    }

    @PostMapping("/user-block")
    public void addBlockUser(@RequestBody MessageEvent messageEvent) throws Exception {
        log.info("addBlockUser : {}", messageEvent);
        eventService.addBlockUser(messageEvent);
    }

    @PostMapping("/user-block-remove")
    public void removeBlockUser(@RequestBody MessageEvent messageEvent) throws Exception {
        log.info("removeBlockUser : {}", messageEvent);
        eventService.removeBlockUser(messageEvent);
    }
}
