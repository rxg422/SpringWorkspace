package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.common.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class XSSHandlingAOP {

	@AfterReturning(pointcut = "CommonPointcut.boardPoint()", returning = "returnObj")
	public void XSSHandle(JoinPoint jp, Object returnObj) {
		// 반환형이 BoardExt인 경우(게시판 상세보기, 게시반 수정) BoardExt 내부 제목, 내용값을 XSS 방어 처리
		if(returnObj instanceof BoardExt) {
			BoardExt b = (BoardExt)returnObj;
			b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
			b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
			b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		}
	}
	
	
}
