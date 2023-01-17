package kr.heeseong.chatting.room.controller;

import kr.heeseong.chatting.event.service.EventService;
import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.user.model.ChattingUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting-room")
public class ChattingRoomController {

    private final EventService eventService;

    @PostMapping("/create")
    public ChattingRoom createChattingRoom(@RequestBody Map<String, String> roomData) throws Exception {
        log.info("createChattingRoom : {}", roomData);
        ChattingRoom createRoom = ChattingRoom.setCreateRoom(roomData);
        return eventService.createChattingRoom(createRoom);
    }

    @PostMapping("/enter-user")
    public ChattingRoom enterChatRoom(@RequestBody ChattingRoom enterUser) throws Exception {
        log.info("enterChatRoom : {}", enterUser);
        return eventService.enterChattingRoom(enterUser);
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

    @GetMapping(value = "/users")
    public ArrayList<ChattingUser> roomUserList(@RequestParam("chattingRoomSeq") Long chattingRoomSeq) throws Exception{
        return eventService.chattingRoomUserList(chattingRoomSeq);
    }
}
