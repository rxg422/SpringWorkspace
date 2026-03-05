package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Board;

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
		return session.selectList("board.selectList", paramMap);
	}

}
