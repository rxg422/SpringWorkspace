package com.kh.spring.security.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;
import com.kh.spring.security.model.vo.MemberExt;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.Post;

@Controller
@RequestMapping("/security")
@Slf4j
public class SecurityController {
	
	// 필드방식 의존성 주입
//	@Autowired
	private MemberService mService;
	
//	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	// 생성자방식 의존성 주입
	// 생성자가 여러개면 @Autowired 어노테이션을 작성해야 한다.
	public SecurityController(MemberService mService, BCryptPasswordEncoder passwordEncoder) {
		this.mService = mService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@RequestMapping("/accessDenied")
	public String accessDenied(Model m) {
		m.addAttribute("errorMsg", "접근 불가 페이지");
		
		return "common/errorPage";
	}
	
	// 회원가입 페이지 이동
	@GetMapping("/insert")
	/*
		@ModelAttribute
			- 커멘드 객체 바인딩시 사용
			- model 영역에 커멘드 객체 저장
	*/
	public String enroll(@ModelAttribute Member member) {
		log.info("bcrypto : {}", "passwordEncoder");
		return "member/memberEnrollForm";
	}
	
	/*
		InitBinder
			- 현재 컨트롤러에서 Binding 작업을 수행할 때 실행되는 객체
			- @ModelAttribute로 지정한 커멘드 객체에 대한 바인딩 작업 수행
		
		처리 순서
			1) 클라이언트의 요청 파라미터를 커멘드 객체 필드에 바인딩 시도
			2) 바인딩 과정에서 WebDataBinder가 필요한 경우 타입변환 및 유효성검사 시행
			3) 유효성 검사 결과를 저장(BindingResult)
	*/
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(new MemberValidator());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		dateFormat.setLenient(false); // yyMMdd 형식이 아닐 경우 에러 발생
		
		// 날빠 형태의 값이 들어오는 경우 자동으로 date 자료형으로 변경
		binder.registerCustomEditor(Date.class, "birthday", new CustomDateEditor(dateFormat, true));
	}
	
	@PostMapping("/insert")
	// BindingResult: 유효성 검사결과를 저장하는 객체, forward시 자동으로 jsp에 전달, form태그 내무에 에러 내용을 바인딩하기 위해 사용
	public String register(@Validated @ModelAttribute Member member,  BindingResult bindingResult, RedirectAttributes ra) {
		// 유효성 검사 실패
		if(bindingResult.hasErrors()) {
			return "member/memberEnrollForm";
		}
		
		// 유효성 검사 성공시 비밀번호 암오화하여 회원가입
		String encryptedPassword = passwordEncoder.encode(member.getUserPwd());
		member.setUserPwd(encryptedPassword);
		
		mService.insertMember(member);
		
		return "redirect:/member/login";
	}
	
	/*
		Authentication
			- Principal : 인증에 사용된 사용자 객체
			- Credentials : 인증에 필요한 비밀번호에 대한 정보를 가진 객체
			- Authorities : 사용자가 가진 권한 정보를 가진 객체
	*/
	@GetMapping("/myPage")
	public String myPage(Authentication auth, Principal principal, Model model) {
		/* 인증된 사용자 정보 가져오기 */
		
		// 1. ArgumentResolver를 이용한 자동 바인딩
		log.debug("auth = {}", auth);
		log.debug("principal = {}", principal);
		
		// 2. SecurityContextHolder 이용
		Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
		MemberExt loginUser = (MemberExt) auth2.getPrincipal();
//		MemberExt loginUser = (MemberExt) prin;
		
		model.addAttribute("loginUser", loginUser);
		
		return "/member/myPage";
	}
	
	@PostMapping("/update")
	public String update(@Validated @ModelAttribute MemberExt loginUser, BindingResult bindResult, Authentication auth, RedirectAttributes ra) {
		if(bindResult.hasErrors()) {
			return "redirect:/security/myPage";
		}
		
		// 비즈니스 로직
		// 1. 전달받은 member 데이터를 바탕으로 db수정 요청
		int result = mService.updateMember(loginUser);
		
		// 2. 정보 수정 성공시 변경된 회원정보를 db에 다시 조회후 새로운 인증정보를 생성하여 SecurityContext에 저장
		if(result > 0) {
			Authentication newAuth = new UsernamePasswordAuthenticationToken(loginUser, auth.getCredentials(), auth.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			ra.addFlashAttribute("alertMsg", "내 정보 수정 성공");
			
			return "redirect:/security/myPage";
		}
		else {
			throw new RuntimeException("회원정보 수정 오류");
		}
	}
	
}
