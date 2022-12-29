package kr.heeseong.chatting.old.controller;

import kr.heeseong.chatting.old.model.MessageEvent;
import kr.heeseong.chatting.old.service.ChattingService_old;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/message")
public class MessageEventController {

	final private ChattingService_old chattingService;
	@Autowired
	public MessageEventController(ChattingService_old chattingService){
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
    		@RequestBody MessageEvent messageEvent) throws Exception {
		System.out.println(22222);
		return chattingService.sendEvent(internalIdx, messageEvent);
	}
}
