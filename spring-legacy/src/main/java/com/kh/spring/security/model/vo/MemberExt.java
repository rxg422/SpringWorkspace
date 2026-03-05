package com.kh.spring.security.model.vo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kh.spring.member.model.vo.Member;

public class MemberExt extends Member implements UserDetails {

	// SimpleGrantedAuthority : 문자열 형태원 권한. "ROLE_USER", "ROLR_ADMIN", "ROLR_MANAGER"...
	private List<SimpleGrantedAuthority> authorities;
	
	// 사용자가 가진 권한 목록 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	// getPassword()/getUsername() : 시큐리티에서 비밀번호/아이디를 가져올 때 사용. id,pw 필드가 username, password가 아닐 때 사용
	@Override
	public String getPassword() {
		return getUserPwd();
	}

	@Override
	public String getUsername() {
		return getUserId();
	}

	// 계정의 만료상태, 잠금상태, 사용가능여부 등을 만환
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
