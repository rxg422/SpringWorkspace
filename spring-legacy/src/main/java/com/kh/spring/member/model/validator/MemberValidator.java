package com.kh.spring.member.model.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.kh.spring.member.model.vo.Member;

public class MemberValidator implements Validator {

	// 유효성 검사를 수행할 클래스 지정 메서드
	@Override
	public boolean supports(Class<?> clazz) {
		return Member.class.isAssignableFrom(clazz);
	}

	// 유효성 검사 메서드
	@Override
	public void validate(Object target, Errors errors) {
		Member member = (Member) target;
		
		// 유효성 검사 로직
		if(member.getUserId() != null) {
			if(!(member.getUserId().length()>=4 && member.getUserId().length()<=20)) {
				errors.rejectValue("userId", "length", "아이디를 4~20자로 작성해주세요.");
			}
			if(!member.getUserId().matches("^[a-zA-Z0-9_]+$")) {
				errors.rejectValue("userId", "pattern", "아이디는 영문, 숫자, _만 사용할 수 있습니다.");
			}
		}
	}

}
