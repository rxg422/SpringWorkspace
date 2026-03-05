package com.kh.spring.board.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	/*
		ResourceLoader
			- 스프링에서 제공하는 자원 로딩 클래스
			- 경로상의 자원(classpath, file시스템, url등)을 동일한 인터페이스로 로드(입력)하는 메서드 제공
	*/
	private final ResourceLoader resourceLoarder;
	private final ServletContext application;

	// BoardType 전역 객체 선언 : 어플리케이션 전역에서 사용할 수 있는 BoardType 객체 추가, 서버 가동중 1회만 수행되도록 설정
	@PostConstruct
	public void init() {
		Map<String, String> boardTypeMap = boardService.getBoardTypeMap();
		application.setAttribute("boardTypeMap", boardTypeMap);
		log.debug("boardTypeMap : {}", boardTypeMap);
	}
	
	/*
		@PathVariable
			- N, P, C, M 등 동적으로 바뀌는 모든 동적 경로 변수를 바인딩
			- 선언한 동적 경로변수는 @PathVariable로 추출하여 사용 가능
			- 추출한 자원은 자동으로 model 영역에 추가
	*/
	@GetMapping("/list/{boardCode}")
	public String selectList(@PathVariable("boardCode") String boardCode, Model model, @RequestParam Map<String, Object> paramMap) {
		/*
			업무로직
			1. 페이징처리
				1) 요청한 게시판 코드와 검색정보와 일치하는 게시클의 총 개수 조회
				2) 게시글 갯수, 페이지 번호, 기본 파라미터들을 추출하여 페이징정보를 가진 객체 생성
			2. 요청한 게시판 코드와 일치하면서 현재 요청한 페이지에 맞는 게시글 조회
			3. 게시글정보, 페이징정보, 검색정보를 담아서 forward
		*/
		// paramMap에 데이터 조회에 필요한 정보 저장
		paramMap.put("boardCd", boardCode);
		List<Board> list = boardService.selectList(paramMap);
		model.addAttribute("list", list);
		
		return "board/boardListView";
	}
	
}
