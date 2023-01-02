package kr.heeseong.chatting.old.controller;

import kr.heeseong.chatting.old.model.MessageEventOld;
import kr.heeseong.chatting.room.model.MessageEvent;
import kr.heeseong.chatting.room.service.ChattingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/message")
public class MessageEventController {

	final private ChattingRoomService chattingService;
	@Autowired
	public MessageEventController(ChattingRoomService chattingService){
		this.chattingService = chattingService;
	}

	@RequestMapping(value="/event", method=RequestMethod.GET)
	public ArrayList<MessageEvent> getEvent(
			@RequestHeader("internalIdx") int internalIdx) throws Exception {
		System.out.println("!");
		return chattingService.getNewEvents(internalIdx);
	}

	@RequestMapping(value="/event", method=RequestMethod.POST)
	public MessageEvent sendEvent(
			@RequestHeader("internalIdx") int internalIdx,
    		@RequestBody MessageEvent messageEventOld) throws Exception {
		System.out.println(22222);
		return chattingService.sendEvent(internalIdx, messageEventOld);
	}
}
