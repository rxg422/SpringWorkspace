package com.kh.spring.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTest {
	
	public static void main(String[] args) {
		/*
			Logging Level
			
			1. fatal
				- 치명적인 에러 출력
			2. error :
				- 요청 처리중 발생하는 오류 출력
				- try-catch의 catch에서 사용
			3. warn
				- 경고성 메세지 출룍
			4. info
				- 요청 처리중 발생하는 정보성 메세지 출력
			5. debug
				- 개발중 필요한 정보성 메세지 출력
			6. trace
				- 상세한 로깅 레벨
				- 디버그보다 많은 내부정보 출력
		*/
		
		log.error("error - {}", "에러 메세지");
		log.warn("warn - {}", "경고 메세지");
		log.info("info - {}", "정보 메세지");
		log.debug("debug - {}", "디버그 메세지");
		log.trace("trace - {}", "트레이스");
	}
	
}
