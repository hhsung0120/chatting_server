package kr.heeseong.chatting.user.model;

import kr.heeseong.chatting.room.model.ChattingRoom;
import kr.heeseong.chatting.room.model.ChattingRoomData;
import kr.heeseong.chatting.room.model.MessageEvent;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class ChattingUserDataTest {

    private ConcurrentHashMap<Long, ChattingRoomData> chattingRooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ChattingUserData> chattingUsers = new ConcurrentHashMap<>();
    private ArrayBlockingQueue<MessageEvent> messageQueue = new ArrayBlockingQueue<>(10);

    @Test
    void QueueTest() throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        queue.put("추가1");
        queue.put("추가2");
        queue.put("추가3");
        queue.put("추가4");
        queue.put("추가5");
        queue.put("추가6");
        queue.put("추가7");
        queue.put("추가8");
        queue.put("추가9");
        queue.put("추가10");
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        System.out.println(queue);
        queue.take();
        System.out.println(queue);
        queue.take();
    }

    @Test
    void finalTest() {
        final Long test;
    }

    @Test
    void stringTest() {
        Map<String, String > test = new HashMap<>();
        System.out.println(test.get("test"));
    }

    @Test
    void longTest() {
        ChattingUser test = new ChattingUser(570L, "test", "test");

        Long test2 = 570L;
        System.out.println(test.getUserIdx() == test2);
    }

    @Test
    void messageTest() throws InterruptedException {

        Map<String, String> roomData = new HashMap<>();
        roomData.put("chattingRoomSeq", "1");
        roomData.put("roomType", "MANY_TO_MANY");
        roomData.put("name", "1");
        roomData.put("description", "description");
        roomData.put("adminIdx", "981");
        roomData.put("categorySeq", "1");
        roomData.put("roomTitle", "2:2");
        roomData.put("password", "1");
        roomData.put("secretModeUseYn", "n");
        roomData.put("simultaneousConnectionsUseYn", "n");
        roomData.put("userIdx", "1");
        roomData.put("userId", "1번유저아이디입니다");
        roomData.put("userName", "1번유저입니다");

        ChattingRoom chattingRoom = ChattingRoom.setCreateRoom(roomData);
        WeakReference<ChattingRoomData> chatRoomRef = new WeakReference<>(new ChattingRoomData());
        ChattingRoomData chattingRoomData = chatRoomRef.get();
        chattingRoomData.setChattingRoom(chattingRoom);
        chattingRooms.put(1L, chattingRoomData);

        ChattingUser user1 = new ChattingUser(1L, "test1", "test1");
        WeakReference<ChattingUserData> userRef1 = new WeakReference<>(new ChattingUserData(user1));
        ChattingUserData chattingUserData1 = userRef1.get();

        ChattingUser user2 = new ChattingUser(2L, "test2", "test2");
        WeakReference<ChattingUserData> userRef2 = new WeakReference<>(new ChattingUserData(user2));
        ChattingUserData chattingUserData2 = userRef2.get();


        chattingUsers.put(chattingUserData1.getUserIdx(), chattingUserData1);
        chattingUsers.put(chattingUserData2.getUserIdx(), chattingUserData2);
        System.out.println(chattingUsers.size());
        System.out.println(chattingUsers);

//        MessageEvent messageEvent1 = new MessageEvent(0, null, null, 126L, "userName_1", "1번유저 메시지 입니다.", "1번 유저 아이디");
//        MessageEvent messageEvent2 = new MessageEvent(0, null, null, 126L, "userName_1", "1번유저 메시지-2 입니다.", "1번 유저 아이디");

//        messageQueue.add(messageEvent1);
//        messageQueue.add(messageEvent2);

        ArrayList<MessageEvent> messageEvents = new ArrayList<>();
        try {
            System.out.println(messageQueue);
            MessageEvent messageEvent = messageQueue.poll(5, TimeUnit.SECONDS);
            if (messageEvent != null && messageQueue != null) {
                messageEvents.add(messageEvent);
                if (messageQueue.size() != 0) {
                    for (int i = 0; i < messageQueue.size(); i++) {
                        messageEvents.add(messageQueue.take());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("=============");
        System.out.println(messageEvents);
        System.out.println("=============");
    }
}