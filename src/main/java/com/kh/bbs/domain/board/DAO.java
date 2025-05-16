package com.kh.bbs.domain.board;

import com.kh.bbs.domain.entity.Board;
import java.util.List;

public interface DAO {
  Long save(Board board);                      // 게시글 등록
  List<Board> findAll();                       // 전체 게시글 조회
  Board findById(Long boardId);                // 게시글 단건 조회
  int update(Long boardId, Board board);       // 게시글 수정
  int delete(Long boardId);// 게시글 삭제
  List<Board> searchByKeyword(String keyword);


}
