package kr.heeseong.chatting.config.task;

import kr.heeseong.chatting.room.service.ChattingRoomService;
import kr.heeseong.chatting.user.service.ChattingUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTask {

    final private ChattingUserService chattingUserService;

    @Scheduled(fixedRate = 60000)
    public void checkUserTimeout() {
        log.info("checkUserTimeout() 동작");
        chattingUserService.checkUsersTimeout();
    }
}