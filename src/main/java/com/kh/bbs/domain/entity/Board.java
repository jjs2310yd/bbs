package com.kh.bbs.domain.entity;

import lombok.Data;

@Data
public class Board {
  private Long boardId;
  private String title;
  private String content;
  private String writer;
  private String createdAt;
}
