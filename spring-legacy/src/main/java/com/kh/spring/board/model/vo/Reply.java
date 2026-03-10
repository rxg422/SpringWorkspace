package com.kh.spring.board.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Reply {

	private int replyNo;
	private String replyContent;
	private String refBno;
	private String replyWriter;
	private String createDate;
	private String status;
	
}
