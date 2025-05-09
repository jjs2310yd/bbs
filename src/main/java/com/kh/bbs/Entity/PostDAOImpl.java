package com.kh.bbs.Entity;

import com.kh.bbs.Entity.PostDAO;
import com.kh.bbs.Entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostDAOImpl implements PostDAO {

  private final NamedParameterJdbcTemplate template;

  @Override
  public Long save(Post post) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO post (post_id, title, content, author) ");
    sql.append("VALUES (post_post_id_seq.nextval, :title, :content, :author) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(post);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    int rows = template.update(sql.toString(), param, keyHolder, new String[]{"post_id"});
    log.info("insert rows={}", rows);

    Number key = keyHolder.getKey();
    return (key != null) ? key.longValue() : null;
  }
}
