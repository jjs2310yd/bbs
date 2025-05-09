package com.kh.bbs.Controller;

import com.kh.bbs.Entity.Post;
import com.kh.bbs.Entity.Post;
import com.kh.bbs.Entity.PostDAO;
import com.kh.bbs.Entity.PostDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostDAO postDAO;

  // 작성 폼
  @GetMapping("/posts/new")
  public String writeForm(Model model) {
    model.addAttribute("post", new Post());
    return "posts/writeForm";
  }

  // 작성 처리
  @PostMapping
  public String write(@ModelAttribute Post post) {
    log.info("post={}", post);
    Long savedId = postDAO.save(post);
    return "redirect:/posts"; // 작성 후 목록으로 리다이렉트
  }

}
