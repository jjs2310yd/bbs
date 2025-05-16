package com.kh.bbs.domain.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {

  private Long boardId;

  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
  private String title;

  @NotBlank(message = "내용을 입력해주세요.")
  private String content;

  private String writer;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
