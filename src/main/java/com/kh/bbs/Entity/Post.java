package com.kh.bbs.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
  private Long postId;
  private String title;
  private String content;
  private String author;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
}
