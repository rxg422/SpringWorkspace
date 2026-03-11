package com.kh.spring.chat.model.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StompController {

	private final ChatService service;
	/*
		SimpMessagingTemplate
			- 서버에서 특정 클라이언트에게 메세지를 전송하기 위한 stomp 템플릿
			- stomp 구독 경로로 메세지를 전송 가능
			
		convertAndSend() : 전체 사용자에게 메세지를 보내는 경우
		convertAndSendToUser() : 특정 사용자에게 메세지를 보내는 경우
	*/
	private final SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/chat/enter/{roomNo}")
	@SendTo("/topic/room/{roomNo}") // 구독경로
	public ChatMessage handleEnter(@DestinationVariable int roomNo, @Payload ChatMessage message) {
		message.setType(ChatMessage.MessageType.ENTER);
		message.setMessage(message.getUserName()+"님이 입장하셨습니다.");
		
		// 메세지 브로커에 메세지 템플릿 전송
		return message;
	}
	
	@MessageMapping("/chat/exit/{roomNo}")
	public void handleExit(@DestinationVariable int roomNo, @Payload ChatMessage message) {
		// 1. 참여자 정보 삭제
		// 2. 채팅방 삭제(참여자 0명일시)
		service.exitChatRoom(message);
		
		// 3. 퇴장메세지 전송
		message.setType(ChatMessage.MessageType.EXIT);
		message.setMessage(message.getUserName()+"님이 퇴장하셨습니다.");
		messagingTemplate.convertAndSend("/topic/room/"+roomNo, message);
	}
	
	@MessageMapping("/notice/send")
	@SendTo("/topic/notice")
	public String sendNotice(@Payload String notice) {
		return notice;
	}
}
