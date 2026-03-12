package com.kh.spring.common.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;

public class Utils {

	// 파일 저장 함수 : 파일을 저장하며 수정된 파일명 반환
	public static String saveFile(MultipartFile upfile, ServletContext application, String boardCd) {
		// 첨부파일 저장경로 설정
		String webPath = "/resources/images/board/" + boardCd + "/";
		// 서버 파일 시스템의 절대경로를 반환하는 메서드
		String serverPath = application.getRealPath(webPath);
		File dir = new File(serverPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// 랜덤 파일명 생성 : 현재시간 + 랜덤값
		String originName = upfile.getOriginalFilename();
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int random = new Random().nextInt(90000) + 10000;
		String ext = originName.substring(originName.lastIndexOf("."));
		String chageName = currentTime + random + ext;
		
		// 서버에 파일 업로드
		try {
			upfile.transferTo(new File(serverPath+chageName));
		}
		catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return webPath+chageName;
	}
	
	/*
		XSS(크로스 사이트 스크립트 공격)
			- 스크립트 삽입 공격
			- 사용자가 스크립트 태그를 게시글에 작성하여 클라이언트가 접근시 스크립트에 지정한 코드가 실행되도록 유도
			- 내용을 db에 그대로 저장하여 브라우저에 렌더링시 문제 발생 따라서 태그가 아닌 기본 문자열로 인식하도록 html 내부 entity로 변환 처리 필수
	*/
	
	public static String XSSHandling(String content) {
        if(content != null) {
            content = content.replaceAll("&", "&amp;");
            content = content.replaceAll("<", "&lt;");
            content = content.replaceAll(">", "&gt;");
            content = content.replaceAll("\"", "&quot;");
        }
        return content;
    }

    // 개행문자 처리
    // textarea -> \n , p -> <br>
    public static String newLineHandling(String content) {
        return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
    }

    // 개행해제 처리
    public static String newLineClear(String content) {
        return content.replaceAll("<br>","\n");
    }
	
}
