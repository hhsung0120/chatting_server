package kr.heeseong.chatting.old.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

	final private ChattingService chattingService;
    @Autowired
    private ScheduledTask(ChattingService chattingService){
        this.chattingService = chattingService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkUserTimeout() {
        chattingService.checkUsersTimeout();
    }
}