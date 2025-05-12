package com.kh.bbs.domain;

import org.springframework.ui.Model;
import com.kh.bbs.domain.board.DAO;
import com.kh.bbs.domain.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

  private final DAO boardDAO;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("boards", boardDAO.findAll());
    return "board/list";
  }

  @GetMapping("/{id}")
  public String view(@PathVariable Long id, Model model) {
    Board board = boardDAO.findById(id);
    model.addAttribute("board", board);
    return "board/view";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("board", new Board());
    return "board/addForm";
  }

  @PostMapping("/add")
  public String save(@ModelAttribute Board board) {
    boardDAO.save(board);
    return "redirect:/board";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("board", boardDAO.findById(id));
    return "board/editForm";
  }

  @PostMapping("/{id}/edit")
  public String update(@PathVariable Long id, @ModelAttribute Board board) {
    boardDAO.update(id, board);
    return "redirect:/board/{id}";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id) {
    boardDAO.delete(id);
    return "redirect:/board";
  }

}
