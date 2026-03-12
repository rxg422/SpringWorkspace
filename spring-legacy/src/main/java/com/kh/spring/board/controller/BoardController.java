package com.kh.spring.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.common.util.Utils;
import com.kh.spring.security.model.vo.MemberExt;

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
	/*
		@RequestParam Map<String, String>
			- 클라이언트가 전달한 파라미터의 key, value 값을 Map 형태로 만들어 바인딩
			- 현재 메서드로 전달할 파라미터의 개수가 정해지지 않은 경우 혹은 일반적인 vo 클래스로 바인딩되지 않는 경우 사용(검색 파라미터)
	*/
	@GetMapping("/list/{boardCode}")
	public String selectList(@PathVariable("boardCode") String boardCode, @RequestParam(value="currentPage", defaultValue= "1") int currentPage, Model model, @RequestParam Map<String, Object> paramMap) {
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
		
		int boardLimit = 10;
		int pageLimit = 10;
		int listCount = boardService.selectListCount(paramMap);
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
		paramMap.put("pi", pi);
		
		List<Board> list = boardService.selectList(paramMap);
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		
		return "board/boardListView";
	}
	
	// 게시판 등록 폼 이동
	@GetMapping("/insert/{boardCd}")
	public String enrollForm(@PathVariable("boardCd") String boardCd, @ModelAttribute Board b, Model m) {
		m.addAttribute("b", b);
		
		return "board/boardEnrollForm";
	}
	
	/*
		List<MultiPartFile>
			- multipart/form-data 방식으로 전송된 파일 데이터를 바인딩
			- 파일의 이름, 크기, 존재여부, 저장기능 등 다양한 메서드 제공
			
		@RequestParam(value="upfile", required=false)
			- name 속성 값이 upfile로 전달되는 모든 파일을 하나의 컬렉션으로 모음
			- @RequestParam + List/Map 사용시 바인딩할 데이터가 없어도 항상 객체 생성
	*/
	@PostMapping("/insert/{boardCd}")
	public String insertBoard(@ModelAttribute Board b, @PathVariable("boardCd") String boardCd, Model model, RedirectAttributes ra, @RequestParam(value="upfile", required=false) List<MultipartFile> upfiles) {
		/*
			업무로직
			1. 유효성검사(생략)
			2. 첨부파일 여부 확인
				1) 첨부파일 존재시 web서버상에 첨부파일 저장
				2) 존재하지 않으면 2번과정 패스
			3. 게시판정보 등록 및 첨부파일 정보 db등록을 위한 서비스 호출
			4. 처리 결과에 따른 view 페이지 지정
				1) 성공시 목록페이지 리디렉트
				2) 실패시 에러 강제 발생 -> ControllerAdvice가 처리
		*/
		// 첨부파일 여부 확인
		// 첨부파일 존재 시 데이터를 담아 dao에 전달
		List<BoardImg> imgList = new ArrayList<>();
		int level = 0; // 첨부파일 레벨 설정
		for(MultipartFile upfile : upfiles) {
			if(upfile.isEmpty()) {
				continue;
			}
			
			// 첨부파일이 존재하면 web서버 상에 첨부파일 저장
			String changeName = Utils.saveFile(upfile, application, boardCd);
			
			// 첨부파일 관리를 위해 db에 첨부파일 위치정보 저장
			BoardImg bi = new BoardImg();
			bi.setChangeName(changeName);
			bi.setOriginName(upfile.getOriginalFilename());
			bi.setImgLevel(level++);
			imgList.add(bi);
		}
		/*
			게시글 등록 서비스
				- 게시들 등록 서비스 호출 전 게시글 정보 바인딩
				- 회원번호, 게시판 코
		*/
		b.setBoardCd(boardCd);
		MemberExt m = (MemberExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		b.setBoardWriter(m.getUserNo()+"");
		
		log.debug("board : {}", b);
		log.debug("imgList : {}", imgList);
		int result = boardService.insertBoard(b, imgList);
		
		if(result <= 0) {
			throw new RuntimeException("게시글 작성 실패");
		}
		ra.addFlashAttribute("alertMsg", "게시글 작성 성공");
		
		return "redirect:/board/list/"+boardCd;
	}
	
	// @CookieValue : Spring에서 지원하는 사용자의 쿠키를 가져오는 방법
	@GetMapping("/detail/{boardCd}/{boardNo}")
	public String selectBoard(@PathVariable("boardCd") String boardCd, @PathVariable("boardNo") int boardNo, Model model, 
			@CookieValue(value="readBoardNo", required=false) String readBoardNoCookie, HttpServletResponse res, Authentication auth) {
		/*
			업무로직
			1. db에서 boardNo와 일치하는 게시글 정보 조회
			2. 조회수 증가 서비스 호출(update)
			3. 게시판 정보 forward
		*/
		// 1. 게시글 정보 조회 - 조회 정보 : 게시글 제목, 내용, 조회수, 작성자, 첨부파일 리스트
		BoardExt b = boardService.selectBoard(boardNo);
		log.debug("게시글 정보 : {}", b);
		
		if(b == null) {
			throw new RuntimeException("게시글이 존재하지 않습니다.");
		}
	
		/*
			게시글 조회수 증가 로직
			
			일반적인 게시판 서비스
				1) 사용자가 게시글을 새로고침하거나, 반복 조회시 조회수 증가
				2) 본인이 작성한 게시글을 본인이 조회할 때도 조회수 증가
			
			일반적인 게시판 서비스 방식은 비효율적. 이를 막기위해선 사용자의 게시글 열람 정보 저장 필요
			
			저장방식
				db에 저장 : 모든 사용자의 게시글 열람기록을 관리함으로 비효율적
				쿠키에 저장 : 클라이언트의 브라우저에 읽을 게시글을 보관, 때문에 백엔드에 미치는 영향이 적어 효율적
				
				저장방식 예시) readBoardNo=11/12/13/14 ...
		*/
		int userNo = ((MemberExt)auth.getPrincipal()).getUserNo();
		if(userNo != Integer.parseInt(b.getBoardWriter())) {
			boolean increase = false;
			
			// readBoardNo 쿠키에 보관된 읽을 게시글 번호 확인
			// 게시글 조회가 처임인 경우
			if(readBoardNoCookie == null) {
				increase = true;
				readBoardNoCookie = boardNo+"";
			}
			// 쿠키가 존재하는 경우
			else {
				// 현재 요청한 게시글 번호와 일치하는 값이 있는지 확인
				List<String> list = Arrays.asList(readBoardNoCookie.split("/"));
				
				// 읽은적 없는 게시물일 경우
				if(list.indexOf(boardNo+"") == -1) {
					increase = true;
					readBoardNoCookie += "/"+boardNo;
				}
			}
			
			if(increase) {
				// 조회수 증가 서비스 호출(update)
				int result = boardService.increaseCount(boardNo);
				
				if(result > 0) {
					b.setCount(b.getCount() + 1);
					
					// 새 쿠키를 생성하여 클라이언트에 전달
					Cookie newCookie = new Cookie("readBoardNo", readBoardNoCookie);
					newCookie.setPath("/spring"); // 쿠키 저장경로
					newCookie.setMaxAge(1 * 60 * 60); // 쿠키 유효시간
					res.addCookie(newCookie);
				}
			}
		}
		
		model.addAttribute("board", b);
		
		return "board/boardDetailView";
	}
	
	@GetMapping("/fileDownload/{boardNo}")
	public ResponseEntity<Resource> fileDownload(@PathVariable("boardNo") int boardNo) {
		/*
			업무로직
			1. 첨부파일 정보 조회(db)
			2. 첨부파일의 changeName을 바탕으로 웹서버 상의 자원 로드
			3. 로드한 첨부파일(resource)를 사용자에게 반환
		*/
		BoardExt b = boardService.selectBoard(boardNo);
		
		if(b.getImgList().isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		// Resource 객체 얻어오기
		String changeName = b.getImgList().get(0).getChangeName();
		String realPath = application.getRealPath(changeName);
		File downFile = new File(realPath);
		
		if(!downFile.exists()) {
			return ResponseEntity.notFound().build();
		}
		
		Resource resource = resourceLoarder.getResource("file:" + realPath);
		
		String filename = "";
		try {
			filename = new String(b.getImgList().get(0).getOriginName().getBytes("utf-8"), "iso-8859-1");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename).body(resource);
	}
	
	@GetMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard(@PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo, Model model, Authentication auth) {
		/*
			업무로직
			1. 현재 게시글을 수정할 수 있는 사용자인기 권한 체크
				- 게시글의 작성자와 로그인한 사용자가 같은경우
				- 관리자 권한을 가진 경우
			2. 권한상 문제가 없는 경우 게시글 조회 후 model에 담아 forward
		*/
		BoardExt board = boardService.selectBoard(boardNo);
		if(board == null) {
			throw new RuntimeException("게시글이 존재하지 않습니다.");
		}

//		int boardWriter = Integer.parseInt(board.getBoardWriter());
//		int userNo = ((MemberExt)auth.getPrincipal()).getUserNo();
//		if(!(boardWriter == userNo || auth.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN")))) {
//			throw new RuntimeException("게시글 수정 권한이 없습니다.");
//		}
		
		model.addAttribute("board", board);
		
		return "board/boardUpdateView";
	}
	
	@PostMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard2(@ModelAttribute Board board, @PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo,
			RedirectAttributes ra, Model model, @RequestParam(value="upfile", required=false) List<MultipartFile> upfiles, String deleteList,
			@RequestParam(value="imgNo", required=false) List<Integer> imgNoList) {
		/*
			업무로직
			
			1. ModelAttribute에 저장된 값 유효성 검사(생략)
			2. 권한 검사(생략)
			3. 새로 등록된 첨부파일 확인 후 저장
		*/
		List<BoardImg> imgList = new ArrayList<BoardImg>();
		for(int i=0, j=0; i<imgNoList.size(); i++) {
			if(!(j < upfiles.size())) {
				break;
			}
			
			MultipartFile upfile = upfiles.get(j++);
			if(upfile.isEmpty()) {
				continue;
			}
			
			String changeName = Utils.saveFile(upfile, application, boardCode);
			BoardImg bi = new BoardImg();
			bi.setBoardImgNo(imgNoList.get(i)); // 수정할 첨부파일 번호
			bi.setChangeName(changeName);
			bi.setOriginName(upfile.getOriginalFilename());
			bi.setImgLevel(i);
			bi.setRefBno(boardNo);
			
			imgList.add(bi);
		}
		board.setBoardCd(boardCode);
		board.setBoardNo(boardNo);
		
		log.debug("board : {}", board);
		log.debug("imgList : {}", imgList);
		log.debug("deleteList : {}", deleteList);
		
		/*
			게시글 및 첨부파일 수정 서비스
			1. 게시글 정보 update(항상)
			2. 첨부파일 정보 수정
				-> insert, update, delete
				1) 새롭게 등록한 첨부파일이 0건 쪼는 deleteList값이 "" -> 우무것도 하지 않음
				2) 첨부파일이 없던 게시글에 새롭게 첨부파일이 생긴 경우 -> insert문 실행
				3) 첨부파일이 있던 게시글에 새로운 파일이 추가된 경우 -> update문 실행
				4) 첨부파일이 있던 게시글에 첨부파일을 제거한 경우 -> delete문 실행
		*/
		int result = boardService.updateBoard(board, deleteList, imgList);
		
		if(result < 0) {
			throw new RuntimeException("게시판 수정 실패");
		}
		
		ra.addFlashAttribute("alertMsg", "게시글 수정 성공");
		
		return "redirect:/board/detail/"+boardCode+"/"+boardNo;
	}
	
}
