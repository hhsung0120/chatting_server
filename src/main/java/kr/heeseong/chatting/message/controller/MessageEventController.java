package kr.heeseong.chatting.message.controller;

import kr.heeseong.chatting.event.service.EventService;
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
    private final EventService eventService;

    @GetMapping(value = "/event")
    public ArrayList<MessageEvent> getEvent(
            @RequestHeader("internalIdx") Long internalIdx) throws Exception {
        System.out.println("internalIdx : " + internalIdx);
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

    @PostMapping(value = "/direct")
    public MessageEvent sendDirectMessage(
            @RequestHeader("internalIdx") Long internalIdx,
            @RequestBody MessageEvent messageEvent) throws Exception {

        log.info("sendDirectMessage : {}", internalIdx);
        return chattingService.sendEvent(internalIdx, messageEvent);
    }

    @PostMapping(value = "/general")
    public MessageEvent sendGeneralMessage(@RequestBody MessageEvent messageEvent) throws Exception {

        log.info("messageEvent : {}", messageEvent);
        return eventService.sendGeneralMessage(messageEvent);
        //return chattingService.sendEvent(internalIdx, messageEvent);
    }
}
