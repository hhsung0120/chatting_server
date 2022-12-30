package kr.heeseong.chatting.old.service;

import kr.heeseong.chatting.room.service.ChattingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    final private ChattingRoomService chattingService;

    @Autowired
    private ScheduledTask(ChattingRoomService chattingService) {
        this.chattingService = chattingService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkUserTimeout() {
        chattingService.checkUsersTimeout();
    }
}