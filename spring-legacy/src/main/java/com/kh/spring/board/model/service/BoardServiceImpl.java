package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardImg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	
	private final BoardDao boardDao;
	
	@Override
	public Map<String, String> getBoardTypeMap() {
		return boardDao.getBoardTypeMap();
	}

	@Override
	public List<Board> selectList(Map<String, Object> paramMap) {
		return boardDao.selectList(paramMap);
	}

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return boardDao.selectListCount(paramMap);
	}

	/*
		@Transactional
			- 선언적 트랜잭션 관리 어노테이션
			- Exception.class와 하위 예외가 발생하면 rollback 처리한다.
			- rollbackFor 미지정시 RuntimeException 에러가 발생한 경우만 rollback;
	*/
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public int insertBoard(Board b, List<BoardImg> imgList) {
		/*
			서비스 로직
			0. 게시글 데이터 전처리(개행문자 처리 및 xss 핸들링)
			1. board 테이블에 데이터 insert
			2. 첨부파일 존재시 첨부테이블에 insert
			3. 오류 발생시 rollback
		*/
		// 1. 게시글 저장 : 게시글 insert 후, boardNo 값을 b객체에 바인딩
		int result = boardDao.insertBoard(b);
		
		if(result == 0) {
			throw new RuntimeException("게시글 등록 실패");
		}
		// 2. 첨부파일 데이터 insert
		if(!imgList.isEmpty()) {
			for(BoardImg bi : imgList) {
				bi.setRefBno(b.getBoardNo());
				// 행 단위 insert
//				result = boardDao.insertBoardImg(bi);
//				
//				if(result == 0) {
//					throw new RuntimeException("첨부파일 등록 실패");
//				}
			}
			
			result = boardDao.insertBoardImgList(imgList);
			
			if(result != imgList.size()) {
				throw new RuntimeException("첨부파일 등록 에러 발생");
			}
		}
		
		return result;
	}

}
