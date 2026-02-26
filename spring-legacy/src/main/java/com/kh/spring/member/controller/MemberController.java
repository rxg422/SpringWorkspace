package com.kh.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

@Controller // ComponentScan에 의해 bean객체로 등록
public class MemberController {
	
	/*
		Spring DI
			- 어플리케이션을 구성하는 객체를 개발자가 생성하는 것이 아닌 스프링이 생성한 객체(bean)을 주입받아 생성
			- new 연산자를 사용하지 않고 Autowird 어노테이션을 통해 주입
	*/	
	@Autowired // 의존성 주입
	private MemberService mService; // = new MemberServiceImpl
	
	@RequestMapping(value="/member/login", method=RequestMethod.GET)
	public String loginMember() {
		return "member/login";
	}
	
	@RequestMapping(value="/member/login" ,method=RequestMethod.POST)
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
	public String login(Member m) {
		System.out.println(m);
		
		return "home";
	}
}
