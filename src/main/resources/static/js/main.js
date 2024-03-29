var userInfo = {
	userMessageIdx : -1,
	messageAdd : ""
};

var isAdmin = false;

function createChatRoom(){
	var randomNumber = Math.floor((Math.random() * 1000) + 1);
	var userId = $('#userInfo-userId').val() != '' ?  $('#userInfo-userId').val() : "userId" + randomNumber;
	var userName = $('#userInfo-userName').val() != '' ? $('#userInfo-userName').val() : randomNumber;
	var userIdx = userId == 'admin' ? 1 : Math.floor((Math.random() * 1000) + 1);
	var chatRoomName = $('#create-chatroom-name').val() != '' ? $('#create-chatroom-name').val() : '1' ;

	//0:MANYTOMANY, 1:ONETOMANY, 2:APPROVAL
	//관리자 여부와 상관없이 무조건 제일 먼저 입장한 사람이 대빵임 //추후 처리 해야함
	var chatRoomType = $('input:radio[class="chatRoomType"]:checked').val();
	var description = $('#create-chatroom-description').val() != '' ? $('#create-chatroom-description').val() : "Description";
	var chattingRoomSeq = chatRoomName;

	//관리자 일 경우 처리
	isAdmin = userId == 'admin' ? true : false;

	ChatClient.setUserInfo(userIdx, userId, userName, isAdmin);

	if(chattingRoomSeq == 1 || chattingRoomSeq == 3){
		ChatClient.createChattingRoom(chattingRoomSeq, userIdx, chatRoomName, description, chatRoomType,function(data) {
			ChatClient.getNewEvent(processEvents);
			drawEnterChatRoom(chattingRoomSeq, data.name, data.userIdx);
		});
	}else{
		if(chattingRoomSeq == 2 || chattingRoomSeq == 4){
			chattingRoomSeq = chattingRoomSeq -1;
		}
		ChatClient.enterChatRoom(chattingRoomSeq, userIdx, chatRoomName, description, chatRoomType,function(data) {
			ChatClient.getNewEvent(processEvents);
			drawEnterChatRoom(chattingRoomSeq, data.name, data.userIdx);
		});
	}
}

function sendMessage(){
	var msg = $("#chat-message").val();
	if (msg && msg.length !== 0) {
		ChatClient.sendMessage(msg, function() {
			$("#chat-message").val('');
		});
	}else{
		alert('메시지를 입력 하세요.')
	}

}
function updateChatRoom(){
	ChatClient.updateChatRoom({chatRoomType:2}, function(data) {
		$('#chat-messages').append('<li class="list-group-item chat-message blocked_message col-lg-12">Room type is changed : ' + data.type + '.</li>');
	});
}

function sendDirectMessage(){
	var userIdx = $('#directmsg-user').attr('data-useridx');
	var msg = $('#direct_message').val();
	if (msg && msg.length !== 0) {
		ChatClient.sendDirectMessage(userIdx, msg, function() {
			$('#direct_message').val('');
		});
	}
	$('#input-direct-message-dialog').modal('hide')
}

function sendAdminMessage(){
	var msg = $('#admin_message').val();
	if (msg && msg.length !== 0) {
		ChatClient.sendAdminMessage(msg, function() {
			$('#admin_message').val('');
		});
	}
}

function leaveChatRoom(){
	exitChatRoom(true);
}

var addUserToUserList = function(userIdx, userId, userName) {
	var newUser = $('<li>', {
		'class': 'list-group-item col-lg-12',
		'id' : 'USER_' +  userIdx,
	});

	var newUserDiv = $('<div>', {
		'class': 'col-lg-12',
		'data-user': userId,
		'data-userIdx' : userIdx,
		'style' : 'padding:0px;'
	}).html(userId + '(' + userName + ')');

	newUser.append(newUserDiv);

	if (ChatClient.getUserIdx() !== userIdx) {
		newUserDiv.addClass('otheruser');
		newUserDiv.click(function(e) {
			console.log($(this).attr('data-userIdx'))
			$('#directmsg-user').attr('data-useridx', userIdx);
			$('#input-direct-message-dialog').modal();
		});

		if (isAdmin) {
			newUserDiv.removeClass('col-lg-12').addClass('col-lg-10');
			var spanBlock = $('<span>', {
				'class': 'col-lg-2 enableblock',
				'style': 'padding:0px;border:1px solid black;text-align:center;',
				'data-user': userIdx,
			}).html('B');

			spanBlock.click(function(e) {
				if (spanBlock.hasClass('enableblock')) {
					var useridx = $(e.target).attr('data-user');
					ChatClient.addBlackList(useridx, function() {
						spanBlock.removeClass('enableblock').addClass('disableblock');
					});
				} else {
					var useridx = $(e.target).attr('data-user');
					ChatClient.removeBlackList(useridx, function() {
						spanBlock.removeClass('disableblock').addClass('enableblock');
					});
				}
			});

			newUser.append(spanBlock);
		}
	}
	$('#user-list').append(newUser);
};

var getUserList = function() {
	ChatClient.getUserList(function(data) {
		$('#user-list').empty();
		if (data && data.length !== 0) {
			data.forEach (function(user) {
				addUserToUserList(user.userIdx, user.userId, user.userName);
			});
		}
	});
};

var drawEnterChatRoom = function(chattingRoomSeq, name) {
	if (chattingRoomSeq !== -1) {
		$('#room-name').html(name);
		$('#chatting-room').show();

		if (isAdmin === true) {
			$('#room-name').removeClass('col-lg-10').addClass('col-lg-8');
			$('#chatroom-admin-btn').show();
		} else {
			$('#room-name').removeClass('col-lg-8').addClass('col-lg-10')
			$('#chatroom-admin-btn').hide();
		}
		getUserList();
	}
};

var exitChatRoom = function(aync) {
	ChatClient.exitChatRoom(aync, function() {
		roomId = -1;
		$('#user-list').empty();
		$('#chat-messages').empty();
		$('#chatting-room').hide();
	});
};

var processEvents = function(events) {
	if (events && events.length > 0) {
		events.forEach (function(event) {
			console.log("event")
			console.log(event)
			console.log("event")
			switch(event.eventType) {
				case eventType.NORMAL_MSG:
					//관리자 화면 admin 일때 자기 자신 아이콘 안보임 (웹적용)
					console.log("일단 여기");
					console.log(eventType.NORMAL_MSG);
					if(event.fromUserIdx==userInfo.userMessageIdx && event.userName == 'admin'){
						$('#chat-messages').append('<li class="admin"><div class="clear"><p class="name fl">'+"Admin"/*event.userName*/+'</p>'+
							'<div class="fr">'+
							'</div></div><div class="cont"><span class="bg_top"></span><p class="txt">'+event.message+'</p>'+
							'<span class="bg_bottom"></span></div></li>');
					}else if(event.fromUserIdx!=userInfo.userMessageIdx && event.userName == 'admin'){
						$('#chat-messages').append('<li class="admin"><div class="clear"><p class="name fl">'+"Admin"/*event.userName*/+'</p>'+
							'<div class="fr"></div></div><div class="cont"><span class="bg_top"></span><p class="txt">'+event.message+'</p>'+
							'<span class="bg_bottom"></span></div></li>');
					}else if(event.fromUserIdx!=userInfo.userMessageIdx && event.userName != 'admin'){
						$('#chat-messages').append('<li class="me"><p class="name"><div class="clear">'+event.userName
							+'<div class="fr">'+
							'</div></div></p><div class="cont"><span class="bg_top"></span><p class="txt">'+event.message+'</p><span class="bg_bottom"></span>'+
							'</div></li>');
					}
					else{
						$("#chat-messages").append('<li class="me"><p class="name">'+event.userName+'</p><div class="cont">'
							+'<span class="bg_top"></span><p class="txt">'+event.message+'</p><span class="bg_bottom"></span>'+
							'</div></li>');
					}
					break;
				case eventType.APPROVED_MSG:
					$('#chat-messages').append('<li class="list-group-item chat-message col-lg-12">msg Approved</li>');
					break;
				case eventType.REJECTED_MSG:
					$('#chat-messages').append('<li class="list-group-item chat-message col-lg-12">msg rejected</li>');
					break;
				case eventType.DIRECT_MSG:
					console.log('DIRECT_MSG')
					$('#chat-messages').append('<li class="list-group-item chat-message direct_message col-lg-12">' + event.userId + '(' + event.userName + ') : ' + event.message + '</li>');
					break;
				case eventType.ADMIN_MSG:
					console.log('ADMIN_MSG')
					$('#chat-messages').append('<li class="list-group-item chat-message admin_message col-lg-12">' + event.userId + '(' + event.userName + ') : ' + event.message + '</li>');
					break;
				case eventType.BLOCKED_MSG:
					console.log('BLOCKED_MSG');
					$('#chat-messages').append('<li class="list-group-item chat-message blocked_message col-lg-12">Your chat is blocked.</li>');
					break;
				case eventType.CREATE_CHATROOM:
//					var tr = $('<tr>', {
//						id: "ROOM_" + event.programIdx
//					}).append('<td>' + event.userName + '</td><td>' + event.userId + '</td><td>' + event.message +'</td>');
//					tr.click(function() {
//						ChatClient.enterChatRoom(room.programIdx, '', '', '', 0, function(data) {
//							drawEnterChatRoom(event.programIdx, data.name, data.userIdx);
//						});
//					});
//					$('#chat-room').append(tr);
					console.log('CREATE_CHATROOM')
					break;
				// case eventType.REMOVE_CHATROOM:
				// 	$('#ROOM_'+ event.programIdx).remove();
				// 	console.log('REMOVE_CHATROOM')
				// 	break;
				case eventType.ENTER_USER:
					addUserToUserList(event.fromUserIdx, event.userId, event.userName);
					console.log('ENTER_USER')
					break;
				case eventType.LEAVE_USER:
					//기존 소스는 event.fromUserIdx 인데 event 안에 데이터가 저렇게 들어있음
					$('#USER_'+ event.from_userIdx).remove();
					console.log('LEAVE_USER')
					break;
				case eventType.REQ_APPROVAL_MSG:
					console.log('REQ_APPROVAL_MSG');
					var req_approval_msg = $('<li>', {
						'class':'list-group-item approve_message col-lg-12'
					}).html('<div class="col-lg-12">' + event.userId + '(' + event.userName + ') : ' + event.message + '</div>')
					var approve_button = $('<button>', {
						'type':'button',
						'class': 'btn btn-default col-lg-6',
					}).html('Approve');
					var reject_button = $('<button>', {
						'type':'button',
						'class': 'btn btn-default col-lg-6',
					}).html('Reject');
					req_approval_msg.append(approve_button);
					req_approval_msg.append(reject_button);

					approve_button.click(function() {
						ChatClient.approveMessage(event.fromUserIdx, event.userId, event.userName, event.message);
					});
					reject_button.click(function() {
						ChatClient.rejectMessage(event.fromUserIdx, event.userId, event.userName, event.message);
					});

					$('#chat-messages').append(req_approval_msg);
					break;
				case eventType.WAIT_APPROVAL_MSG:
					console.log('WAIT_APPROVAL_MSG');
					$('#chat-messages').append('<li class="list-group-item chat-message col-lg-12">Wait Approval message : ' + event.message + '</li>');
					break;
			}
		});
	}
};

