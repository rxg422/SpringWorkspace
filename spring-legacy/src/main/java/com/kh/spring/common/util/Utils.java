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
	
}
