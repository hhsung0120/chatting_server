var eventType = {
	NORMAL_MSG: 'NORMAL_MSG',
	DIRECT_MSG: "DIRECT_MSG",
	ADMIN_MSG: "ADMIN_MSG",
	REQ_APPROVAL_MSG: "REQ_APPROVAL_MSG",
	WAIT_APPROVAL_MSG: "WAIT_APPROVAL_MSG",
	APPROVED_MSG: "APPROVED_MSG",
	REJECTED_MSG: "REJECTED_MSG",
	BLOCKED_MSG: "BLOCKED_MSG",
	CREATE_CHATROOM: "CREATE_CHATROOM",
	REMOVE_CHATROOM: "REMOVE_CHATROOM", //마지막에 나간 사람
	ADD_BLACKLIST: "ADD_BLACKLIST",
	REMOVE_BLACKLIST: "REMOVE_BLACKLIST",
	LIST_BLACKLIST: "LIST_BLACKLIST",
	ENTER_USER: "ENTER_USER", //접속
	LEAVE_USER: "LEAVE_USER" //나감
	// 30 내가 추가  채팅 승인하면 30 됨 5번과 동시에 db 인설트 되며, 필요에 의해서 30만들었음
	// false는  비정상 종료, true 버튼 종료
};
//
// var ChatRoomType = {
// 	MANYTOMANY : 0, //다대다
// 	ONETOMANY : 1, //일대다 관리자 : 사용자
// 	APPROVAL : 2 //승인 되어야 전송 됨
// }






















