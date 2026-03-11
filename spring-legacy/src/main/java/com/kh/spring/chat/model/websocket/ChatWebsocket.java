package com.kh.spring.chat.model.websocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//TextWebSocketHandler : 텍스트 기반 메세지 처리를 위한 메서드를 제공하는 클래스
@Slf4j
@RequiredArgsConstructor
public class ChatWebsocket extends TextWebSocketHandler {
	
	private final ChatService cService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	// 채팅방별 데이터 관리를 위한 Map 객체
	private final Map<Integer, Set<WebSocketSession>> roomSession = new HashMap<>();
	
	// 클라이언트, 서버 웹소켓 연결 완료 이후 실행 함수
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		/* 		
			클라이언트와 연결시 session에 담겨있는 채팅방 번호를 꺼내 현재 서버에 저장
			
			WebSocketSession
			 - 클라이언트가 웹소켓을 통해 연결하고 있는 동안만 유지되는 세션
			 - 사용자가 페이지를 새로고침하거나 브라우저를 닫으면 세션 종료, 새로운 세션 생성
		*/
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.debug("웹소켓 연결 완료. 세션 id : {}, 채팅방 번호 : {}", session.getId(), chatRoomNo);
		
		Set<WebSocketSession> sessions = roomSession.get(chatRoomNo);
		// 현재 채팅방 번호에 set 객체가 없는 경우, 새로 생성 후 데이터 추가
		if(sessions == null) {
			sessions = new HashSet<>();
			roomSession.put(chatRoomNo, sessions);
		}
		sessions.add(session);
	}
	
	// 클라이언트가 메세지 전달시 실행되는 함수
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TextMessage : 웹소켓을 이용해 전달된 데이터가 담긴 객체
		log.debug("메세지 : {}", message);
		log.debug("메세지 body : {}", message.getPayload());
		
		// JSON -> VO
		ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
		
		// 전달받은 메세지 DB 등록
		int result = cService.insertMessage(chatMessage);
		
		String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		chatMessage.setCreateDate(current);
		
		if(result > 0) {
			// 전달받은 메세지를 같은 채팅방의 모든 사용자에게 전송
			Set<WebSocketSession> roomSet = roomSession.get(chatMessage.getChatRoomNo());
			if(roomSet != null) {
				for(WebSocketSession s : roomSet) {
					String json = objectMapper.writeValueAsString(chatMessage);
					s.sendMessage(new TextMessage(json));
				}
			}
		}
	}
	
	// 웹소켓 연결 종료 후 실행되는 메서드
	// 클라이언트의 session 정보를 메모리에서 제거
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.debug("웹소켓 연결 종료");
		Set<WebSocketSession> roomSet = roomSession.get(chatRoomNo);
		
		if(roomSet != null) {
			roomSet.remove(session);
			
			if(roomSet.isEmpty()) {
				roomSession.remove(chatRoomNo);
			}
		}
	}
	
}
