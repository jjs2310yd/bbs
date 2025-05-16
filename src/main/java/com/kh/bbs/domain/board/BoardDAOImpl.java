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
      board.setCreatedAt(rs.getTimestamp("created_at") != null ?
          rs.getTimestamp("created_at").toLocalDateTime() : null);
      board.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
          rs.getTimestamp("updated_at").toLocalDateTime() : null);
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

    Number key = keyHolder.getKey(); // getKey() 사용
    if (key == null) {
      log.error("Failed to retrieve generated key for board."); // 로그 추가
      throw new IllegalStateException("Failed to retrieve generated key for board.");
    }
    return key.longValue();
  }


  @Override
  public List<Board> findAll() {
    String sql = "SELECT board_id, title, content, writer, created_at, updated_at " +
        "FROM board ORDER BY board_id DESC";
    return template.query(sql, boardRowMapper());
  }

  @Override
  public Board findById(Long boardId) {
    String sql = "SELECT board_id, title, content, writer, created_at, updated_at  " +
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
  @Override
  public List<Board> searchByKeyword(String keyword) {
    String sql = "SELECT * FROM board " +
        "WHERE LOWER(title) LIKE LOWER(:keyword) " +
        "   OR LOWER(content) LIKE LOWER(:keyword) " +
        "   OR LOWER(writer) LIKE LOWER(:keyword) " +
        "ORDER BY board_id DESC";

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("keyword", "%" + keyword + "%");

    return template.query(sql, param, boardRowMapper());
  }


}
