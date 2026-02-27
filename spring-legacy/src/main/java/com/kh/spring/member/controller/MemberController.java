package com.kh.spring.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

@Controller // ComponentScan에 의해 bean객체로 등록
@RequestMapping(value="/member")
@SessionAttributes({"loginUser"}) // model에 저장되는 데이터 중, Session에 저장할 데이터를 설정
public class MemberController {
	
	/*
		Spring DI
			- 어플리케이션을 구성하는 객체를 개발자가 생성하는 것이 아닌 스프링이 생성한 객체(bean)을 주입받아 생성
			- new 연산자를 사용하지 않고 Autowird 어노테이션을 통해 주입
	*/	
	@Autowired // 의존성 주입
	private MemberService mService; // = new MemberServiceImpl
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginMember() {
		return "member/login";
	}
	
//	@RequestMapping(value="/member/login" ,method=RequestMethod.POST)
	@PostMapping("/login")
	public ModelAndView login(@ModelAttribute Member m, ModelAndView mv, Model model, HttpSession session, RedirectAttributes ra) {
		// 로그인 요청 처리
		Member loginUser = mService.loginMember(m);
		
		// 로그인 성공시
		if(loginUser != null) {
			// 인증된 사용자 정보를 session에 보관
//			session.setAttribute("loginUser", loginUser);
			model.addAttribute("loginUser", loginUser);
		}
		else {
//			session.setAttribute("alertMsg", "로그인 실패");
			/* RedirectAttributes의 flashAttribute는 데이터를 SessionScope에 저장, 이후 redirect 완료시 SessionScope의 데이터를 requestScope로 변경 */
			ra.addFlashAttribute("alertMsg", "로그인 실패");
		}
		
		// 메인 페이지로 redirect
		mv.setViewName("redirect:/");
		
		return mv;
	}
	
//	public String login(HttpServletRequest req) {
//		System.out.println(req.getParameter("userId"));
//		System.out.println(req.getParameter("userPwd"));
//		
//		return "home";
//	}
	/*
	 	@RequestParam
	 		- Servlet의 request.getParameter(키)를 대신ㅏ는 어노테이션
	 		- 클라이언트가 요청한 값을 대신 변환하여 바인당 하는 역할은 ArgumentResolver가 수행
	*/
//	public String login(@RequestParam(value="userId", defaultValue="test01") String userId) {
//		System.out.println(userId);
//		
//		return "home";
//	}
	// 매개변수명과 일치하는 request 파라미터값을 자동바인딩. 일치하는 파라미터가 없으면 null 바인딩
//	public String login(String userId, String userPwd) {
//		System.out.println(userId);
//		System.out.println(userPwd);
//		
//		return "home";
//	}
	/*
		커맨드 객체 방식(@ModelAttribute : 생략가능)
			- 메서드의 매개변수로 VO클래스 타입을 지정하는 경우
			- 요청시 전달한 name 속성과 일치하는 VO클래스 필드의 setter()를 호출 및 바인딩
	*/
//	public String login(@ModelAttribute Member m) {
//		System.out.println(m);
//		
//		return "home";
//	}
	/*
		Model : 응답데이터 저장 객체
		1) Model
			- 포워딩할 응답 뷰페이지에 전달하려는 데이터를 맵형식으로 저장하는 객체
			- 기본적으로 request scope를 가지며, 설정을 통해 session scope에 데이터 저장 가능
			- 클래스 선언부에 @SessionAttributes를 추가하여 session scope에 데이터 저장 가능
		2) ModelAndView
			- Model과 이동할 페이지에 대한 정보를 가진 객체인 VIEW를 합친 객체
			- 기본적으로 request scope에 데이터 저장
	*/
//	public ModelAndView login(Member m, ModelAndView mv, Model model) {
//		model.addAttribute("errorMsg", "오류 발생(Model)!");
//		
//		mv.addObject("errorMsg", "오류 발생!(ModelAndView)");
//		mv.setViewName("common/errorPage");
//		
//		return mv;
//	}
	
	/* 현재 세션의 인증정보를 만료시켜 로그아웃 */
	@GetMapping("/logout")
	public String logout(HttpSession session, SessionStatus status) {
		session.invalidate(); // session 내부 데이터 삭제
		status.setComplete(); // model로 sessionScope에 이관된 데이터 삭제
		
		return "redirect:/";
	}
	
	@GetMapping("/insert")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	@PostMapping("/insert")
	public String insertMember(Member m, Model model, RedirectAttributes ra) {
		int result = mService.insertMember(m);
		
		if(result > 0) {
			ra.addFlashAttribute("alertMsg", "회원가입이 정상적으로 처리되었습니다.");
			return "redirect:/member/login";
		}
		else {
			throw new RuntimeException();
//			model.addAttribute("errorMsg", "회원가입에 실패하였습니다.");
//			return "common/errorPage";
		}
	}
	
	// 비동기 요청 처리
	@ResponseBody // 반환값을 주소가 아닌 값으로 처리
	@GetMapping("/idCheck")
	public int idCheck(String userId) {
		int result = mService.idCheck(userId);
		
		return result;
	}
	
//	@ResponseBody
//	@GetMapping("/selectOne")
//	public Member selectOne(String userId) {
//		Member m = mService.selectOne(userId);
//		
//		// Jackson-databind를 활용하여 VO클래스, 컬렉션 데이터를 JSON으로 반환 가능 
//		return m;
//	}

	@GetMapping("/selectOne")
	public ResponseEntity<Member> selectOne(String userId) {
		Member m = mService.selectOne(userId);
		ResponseEntity<Member> res = null;
		
		if(m != null) {
			// ok() : 응답상태 200
			res = ResponseEntity.ok(m);
		}
		else {
			// notFound() : 응답상태 404
			res = ResponseEntity.notFound().build(); 
		}
		
		return res;
	}
	
	
}
