package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.common.model.vo.PageInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BoardDaoImpl implements BoardDao {
	
	private final SqlSessionTemplate session; 
	
	@Override
	public Map<String, String> getBoardTypeMap() {
		/*
			selectMap
				- Map<K,V> 형태의 값을 반환
				- 두번째 매개변수로 어떤 컬럼을 key 값으로 사용할지 작성
				- select key, value from table
		*/
		return session.selectMap("board.getBoardTypeMap", "boardCd");
	}

	@Override
	public List<Board> selectList(Map<String, Object> paramMap) {
		/*
			페이징 처리 : 특정 페이지의 데이터를 가져오는 방법
			1. ROWNUM, ROW_NUMBER()로 페이징 처리된 쿼리 조회
				SELECT * FROM (SELECT ROWNUM AS RNUM , T.* FROM (SELECT ...) T) WHERE RNUM BETWEEN A AND B
			2. OFFSET FETCH를 사용하여 쿼리 조회(오라클 12이상에서 사용)
				- 코드의 복잡성을 줄이고 가독성을 확보한 페이징 방식
				SELECT 조회컬럼 FROM 테이블 WHERE 조건 ORDER BY 정렬기준컬럼 OFFSET 시작행 ROWS FETCH NEXT 조회개수 ROWS ONLY
			3. RowBounds를 활용한 방식
				- MyBatis에서 쿼리 결과에 대해 페이징 처리를 적용해주는 도구
				- 전체 쿼리 결과에서 자바 어플리케이션으로 가져온 후 지정된 위치(offset)에서 특정 개수(limit)를 잘라내는 방식
				- 오라클 offset fetch 문법과 비슷, 어플리케이션으로 가져올 데이터가 수만건 이상인 경우 심각한 메모리 낭비 및 성능저하 발생 가능
				- 소규모 데이터 쿼리시 사용 권장
		*/
		PageInfo pi = (PageInfo) paramMap.get("pi");
		int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
		int limit = pi.getBoardLimit();
		// 1. RowBounds 방식을 활용한 페이징 처리 : 몇번째 행부터 몇개 가져올 것인지 지정
//		RowBounds rowBounds = new RowBounds(offset, limit);
//		
//		return session.selectList("board.selectList", paramMap, rowBounds);
		
		// 2. Rownum을 활용한 페이징 처리
		paramMap.put("offset", offset + 1);
		paramMap.put("limit", limit+offset);
		
		return session.selectList("board.selectList", paramMap);
	}

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return session.selectOne("board.selectListCount", paramMap);
	}

	@Override
	public int insertBoard(Board b) {
		return session.insert("board.insertBoard", b);
	}

	@Override
	public int insertBoardImgList(List<BoardImg> imgList) {
		return session.insert("board.insertBoardImgList", imgList);
	}

	@Override
	public BoardExt selectBoard(int boardNo) {
		return session.selectOne("board.selectBoard", boardNo);
	}

	@Override
	public int increaseCount(int boardNo) {
		return session.update("board.increaseCount", boardNo);
	}

	@Override
	public int updateBoard(Board board) {
		return session.update("board.updateBoard", board);
	}

	@Override
	public int insertBoardImg(BoardImg bi) {
		return session.insert("board.insertBoardImg", bi);
	}

	@Override
	public int updateBoardImg(BoardImg bi) {
		return session.update("board.updateBoardImg", bi);
	}

	@Override
	public int deleteBoardImg(String deleteList) {
		return session.delete("board.deleteBoardImg", deleteList);
	}

}
