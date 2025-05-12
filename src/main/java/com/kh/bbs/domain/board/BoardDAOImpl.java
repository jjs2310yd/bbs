package com.kh.bbs.domain.board;

import com.kh.bbs.domain.entity.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BoardDAOImpl implements com.kh.bbs.domain.board.DAO {

  private final NamedParameterJdbcTemplate template;

  private RowMapper<Board> boardRowMapper() {
    return (rs, rowNum) -> {
      Board board = new Board();
      board.setBoardId(rs.getLong("board_id"));
      board.setTitle(rs.getString("title"));
      board.setContent(rs.getString("content"));
      board.setWriter(rs.getString("writer"));
      board.setCreatedAt(rs.getString("created_at"));
      return board;
    };
  }

  @Override
  public Long save(Board board) {
    String sql = "INSERT INTO board (board_id, title, content, writer, created_at) " +
        "VALUES (BOARD_BOARD_ID_SEQ.nextval, :title, :content, :writer, sysdate)";
    SqlParameterSource param = new BeanPropertySqlParameterSource(board);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql, param, keyHolder, new String[]{"board_id"});

    Number key = keyHolder.getKeys() != null ? (Number) keyHolder.getKeys().get("board_id") : null;
    if (key == null) {
      throw new IllegalStateException("게시글 번호를 생성할 수 없습니다.");
    }
    return key.longValue();
  }

  @Override
  public List<Board> findAll() {
    String sql = "SELECT board_id, title, content, writer, created_at " +
        "FROM board ORDER BY board_id DESC";
    return template.query(sql, boardRowMapper());
  }

  @Override
  public Board findById(Long boardId) {
    String sql = "SELECT board_id, title, content, writer, created_at " +
        "FROM board WHERE board_id = :boardId";
    SqlParameterSource param = new MapSqlParameterSource("boardId", boardId);
    return template.queryForObject(sql, param, boardRowMapper());
  }

  @Override
  public int update(Long boardId, Board board) {
    String sql = "UPDATE board SET title = :title, content = :content, writer = :writer " +
        "WHERE board_id = :boardId";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("title", board.getTitle())
        .addValue("content", board.getContent())
        .addValue("writer", board.getWriter())
        .addValue("boardId", boardId);
    return template.update(sql, param);
  }

  @Override
  public int delete(Long boardId) {
    String sql = "DELETE FROM board WHERE board_id = :boardId";
    SqlParameterSource param = new MapSqlParameterSource("boardId", boardId);
    return template.update(sql, param);
  }
}
