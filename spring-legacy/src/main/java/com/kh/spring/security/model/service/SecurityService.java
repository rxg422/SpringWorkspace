package com.kh.spring.security.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/*
	UserDetailsService
		- 스프링 Security에서 인증 처리시 사용하는 인터페이스
		- 사용자 정보를 조회하는 메서드인 loadUserByUserName()을 정의하고 있다.
	
	loadUserByUserName()
		- 사용자의 아이디를 기반으로 UserDetails 객체를 반환하는 메서드
		- 반환된 UserDetails는 비밀번호 및 권한 검사시 사용
		- 일반덕으로 DB에 저장된 사용자 정보를 조회하는 방식으로 비지니스 로직을 작성
*/
public interface SecurityService extends UserDetailsService {
	
}
