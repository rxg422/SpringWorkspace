package com.kh.spring.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
	
	/*
		Spring 예외처리 방법
	
		1. try-catch로 메서드별 예외처리 -> 1순위 적용
		2. @ExceptionHandler
			- 하나의 컨트롤러에서 발생하는 예외들을 모아 처리
			- 컨트롤러에 예외처리 메서드를 1개 추가 후 @ExceptionHandler 어노테이션 추가 
			- 2순위 적용
		3. @ControllerAdvice
			- 어플리케이션 전역에서 발생하는 예외를 모아서 처리
			- 3순위로 적용됨
	*/
	
	@ExceptionHandler
	public String exceptionHandler(Exception e, Model model) {
		e.printStackTrace();
		
		model.addAttribute("errorMsg", "서비스 이용중 문제가 발생하였습니다.");
		
		return "common/errorPage";
	}
	
}
