package kr.heeseong.chatting.message.controller;

import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageEventController {

    private final ChattingRoomService chattingService;

    @GetMapping(value = "/event")
    public ArrayList<MessageEvent> getEvent(
            @RequestHeader("internalIdx") Long internalIdx) throws Exception {
        return chattingService.getNewEvents(internalIdx);
    }

    @PostMapping(value = "/event")
    public MessageEvent sendEvent(
            @RequestHeader("internalIdx") Long internalIdx,
            @RequestBody MessageEvent messageEvent) throws Exception {

        log.info("internalIdx : {}", internalIdx);
        log.info("messageEvent : {}", messageEvent);
        return chattingService.sendEvent(internalIdx, messageEvent);
    }
}
