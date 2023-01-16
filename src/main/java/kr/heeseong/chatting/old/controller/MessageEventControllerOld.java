package kr.heeseong.chatting.old.controller;

import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/messageOld")
public class MessageEventControllerOld {

    final private ChattingRoomService chattingService;

    @Autowired
    public MessageEventControllerOld(ChattingRoomService chattingService) {
        this.chattingService = chattingService;
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public ArrayList<MessageEvent> getEventOld(
            @RequestHeader("internalIdx") Long internalIdx) throws Exception {
        return chattingService.getNewEvents(internalIdx);
    }

//    @RequestMapping(value = "/event", method = RequestMethod.POST)
//    public MessageEvent sendEventOld(
//            @RequestHeader("internalIdx") Long internalIdx,
//            @RequestBody MessageEvent messageEventOld) throws Exception {
//        System.out.println(22222);
//        return chattingService.sendEvent(internalIdx, messageEventOld);
//    }
}
