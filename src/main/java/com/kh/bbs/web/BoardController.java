package com.kh.bbs.web; // 네 Controller 패키지 경로

import jakarta.validation.Valid; // Bean Validation (Jakarta Validation) 어노테이션 사용
import org.springframework.ui.Model; // Spring MVC 모델 객체
import com.kh.bbs.domain.board.DAO; // Board 관련 데이터 접근 인터페이스
import com.kh.bbs.domain.entity.Board; // 게시글 정보를 담는 엔티티 클래스 (여기에 @NotBlank, @Size 등 유효성 어노테이션 추가 필요!)
import lombok.RequiredArgsConstructor; // Lombok 라이브러리: final 필드 생성자 자동 생성
import lombok.extern.slf4j.Slf4j; // Lombok 라이브러리: Logger 자동 생성
import org.springframework.stereotype.Controller; // Spring 빈: Controller 역할
import org.springframework.validation.BindingResult; // 유효성 검사 결과 객체
import org.springframework.web.bind.annotation.*; // Spring Web 어노테이션 (GetMapping, PostMapping 등)
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 리다이렉트 시 데이터 전달 객체

import java.util.List;


@Slf4j // 로그 사용 설정
@Controller // 웹 요청을 처리하는 컨트롤러 역할
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (BoardDAO 주입)
@RequestMapping("/board") // 이 컨트롤러 내부의 모든 요청 매핑은 "/board" 경로로 시작합니다.
public class BoardController {

  // BoardDAO 인터페이스를 구현한 객체(BoardDAOImpl)를 Spring이 주입해 줍니다.
  // 이 객체를 통해 데이터베이스 연동 작업을 수행합니다.
  private final DAO boardDAO;

  // 게시글 목록 조회 핸들러 메소드
  // HTTP GET 요청이 "/board" 경로로 들어왔을 때 이 메소드가 실행됩니다.
  // GET http://localhost:9080/board
  @GetMapping
  public String list(Model model) {
    log.info("### [GET] /board - 게시글 목록 요청"); // 요청 발생 시 로그 기록

    // boardDAO의 findAll() 메소드를 호출하여 데이터베이스에서 모든 게시글 목록을 가져옵니다.
    // 가져온 목록을 "boards"라는 이름으로 Model 객체에 담습니다.
    // 이 데이터는 뷰(board/list.html)에서 ${boards} 이름으로 접근할 수 있습니다.
    model.addAttribute("boards", boardDAO.findAll());

    // "board/list"라는 뷰 이름(Thymeleaf 템플릿 파일 경로)을 반환합니다.
    // Spring은 이 뷰 이름을 기반으로 src/main/resources/templates/board/list.html 파일을 찾아 렌더링합니다.
    return "board/list";
  }

  // 게시글 상세 조회 핸들러 메소드
  // HTTP GET 요청이 "/board/{id}" 경로로 들어왔을 때 이 메소드가 실행됩니다.
  // {id} 부분은 경로 변수로, 실제 게시글 ID 값이 들어옵니다.
  // 예: GET http://localhost:9080/board/10
  @GetMapping("/{id}")
  public String view(@PathVariable("id") Long id, Model model) { // @PathVariable로 URL 경로의 {id} 값을 Long 타입 변수 id로 받아옵니다.
    log.info("### [GET] /board/{} - 게시글 상세 요청", id); // 요청 발생 시 로그 기록

    // boardDAO의 findById() 메소드를 호출하여 해당 ID의 게시글을 데이터베이스에서 조회합니다.
    Board board = boardDAO.findById(id);

    // 조회된 게시글이 존재하지 않을 경우 (findById가 null을 반환)
    if (board == null) {
      log.warn("존재하지 않는 게시글 조회 시도: {}", id); // 경고 로그 기록
      return "error/404";
    }

    model.addAttribute("board", board);

    // "board/view"라는 뷰 이름(Thymeleaf 템플릿 파일 경로)을 반환합니다.
    // Spring은 이 뷰 이름을 기반으로 src/main/resources/templates/board/view.html 파일을 찾아 렌더링합니다.
    return "board/view";
  }

  // 게시글 작성 화면 핸들러 메소드
  // HTTP GET 요청이 "/board/add" 경로로 들어왔을 때 이 메소드가 실행됩니다.
  @GetMapping("/add")
  public String addForm(Model model) { // 폼 화면을 보여주는 메소드는 addForm이라는 이름을 자주 사용합니다.
    log.info("### [GET] /board/add - 게시글 작성 화면 요청"); // 요청 발생 시 로그 기록

    // 새로운 빈 Board 객체를 생성하여 "board"라는 이름으로 Model 객체에 담습니다.
    // 이 객체는 뷰(board/addForm.html)의 폼 태그(th:object="${board}")와 바인딩되어 사용됩니다.
    model.addAttribute("board", new Board());

    // "board/addForm"이라는 뷰 이름(Thymeleaf 템플릿 파일 경로)을 반환합니다.
    // Spring은 이 뷰 이름을 기반으로 src/main/resources/templates/board/addForm.html 파일을 찾아 렌더링합니다.
    return "board/addForm";
  }

  // 게시글 등록 처리 핸들러 메소드
  // POST http://localhost:9080/board/add
  @PostMapping("/add")
  public String save(@Valid @ModelAttribute("board") Board board, // @Valid: 유효성 검사, @ModelAttribute("board"): 폼 데이터 바인딩 및 모델 추가
                     BindingResult bindingResult,  // 유효성 검사 결과
                     RedirectAttributes redirectAttributes, // 리다이렉트 시 데이터 전달
                     Model model // 유효성 검사 실패 시 모델
  ) {
    log.info("### [POST] /board/add - 게시글 등록 처리 진입");
    log.info("### 넘어온 Board 객체 (검증 전): {}", board);

    // 1. 유효성 검사 실패 시
    if (bindingResult.hasErrors()) {
      log.warn("### 게시글 등록 유효성 검사 오류 발생: {}", bindingResult);
      // 유효성 검사 실패 정보와 입력 데이터는 모델에 자동으로 담겨 뷰로 전달됩니다.
      return "board/addForm"; // 글 작성 폼 화면으로 다시 돌아가 에러 메시지 표시
    }

    // 2. 유효성 검사 통과 시
    log.info("### 유효성 검사 성공! 게시글 저장 시도: {}", board);

    // DB 저장 중 예외 처리
    try {
      Long savedBoardId = boardDAO.save(board); // DAO 호출하여 저장, 생성된 ID 반환 받음
      log.info("### 게시글 저장 성공! 생성된 ID: {}", savedBoardId);

      // 등록 성공 메시지 Flash Attribute에 담기
      redirectAttributes.addFlashAttribute("status", "게시글이 성공적으로 등록되었습니다!");
      // 저장 후 상세 페이지로 리다이렉트 (URL 경로에 ID 포함)
      return "redirect:/board/" + savedBoardId;

    } catch (Exception e) {
      log.error("### 게시글 저장 중 DB 오류 발생", e); // DB 에러 발생 시 로그
      model.addAttribute("board", board); // 입력 데이터 유지
      model.addAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다: " + e.getMessage()); // 에러 메시지 모델에 담기
      return "board/addForm"; // 글 작성 폼 화면으로 돌아가 에러 메시지 표시
    }
  }


  // 게시글 수정 화면 핸들러 메소드
  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable("id") Long id, Model model) { // @PathVariable로 {id} 값 받음
    log.info("### [GET] /board/{}/edit - 게시글 수정 화면 요청", id);

    Board board = boardDAO.findById(id); // DAO 호출하여 게시글 조회

    // 조회된 게시글이 null이면 404 에러 처리
    if (board == null) {
      log.warn("존재하지 않는 게시글 수정 폼 요청: {}", id);
      return "error/404"; // error/404.html 뷰 반환
    }

    // 조회된 게시글 객체를 모델에 "board" 이름으로 담기 (editForm.html에서 ${board}로 사용)
    model.addAttribute("board", board);

    return "board/editForm"; // board/editForm.html 뷰 반환
  }

  // 게시글 수정 처리 핸들러 메소드
  @PostMapping("/{id}/edit")
  public String update(@PathVariable("id") Long id, // URL 경로 변수 {id} 값
                       @Valid @ModelAttribute("board") Board board, // 폼 데이터 바인딩 및 유효성 검사
                       BindingResult bindingResult, // 유효성 검사 결과
                       RedirectAttributes redirectAttributes, // 리다이렉트 시 데이터 전달
                       Model model // 유효성 검사 실패 시 모델
  ) {
    log.info("### [POST] /board/{}/edit - 게시글 수정 처리 진입", id);
    log.info("### 넘어온 Board 객체 (검증 전): {}", board);
    log.info("### 경로 변수 ID: {}", id);

    // 1. URL ID와 폼 객체의 ID 일치 검증
    // 폼 객체의 boardId는 hidden 필드로 넘어옴. URL ID와 일치하는지 확인.
    if (board.getBoardId() == null || !id.equals(board.getBoardId())) {
      log.warn("### URL ID({})와 Board ID({}) 불일치 또는 폼 객체 ID 누락", id, board.getBoardId());
      // 불일치 시 유효성 검사 결과에 에러 추가
      bindingResult.rejectValue("boardId", "idMismatch", "게시글 ID가 일치하지 않거나 누락되었습니다.");
    }

    // 2. 유효성 검사 실패 시
    if (bindingResult.hasErrors()) {
      log.warn("### 게시글 수정 유효성 검사 오류 발생: {}", bindingResult);
      // 유효성 검사 실패 정보와 입력 데이터는 모델에 자동으로 담겨 뷰로 전달됩니다.
      return "board/editForm"; // 수정 폼 화면으로 돌아가 에러 메시지 표시
    }

    // 3. 유효성 검사 통과 시
    log.info("### 유효성 검사 성공! 게시글 수정 시도: {}", board);

    // DB 수정 중 예외 처리
    try {
      int updatedRows = boardDAO.update(id, board); // DAO 호출하여 수정, 업데이트된 행 개수 반환
      log.info("### 게시글 수정 결과: {}건 업데이트됨", updatedRows);

      // 수정된 행이 0건일 경우 (게시글 없거나 수정 실패)
      if (updatedRows == 0) {
        log.warn("### 수정된 게시글이 없음. ID: {}", id);
        model.addAttribute("errorMessage", "게시글이 존재하지 않거나, 수정되지 않았습니다."); // 에러 메시지 모델에 담기
        return "board/editForm"; // 수정 폼 화면으로 돌아가 에러 메시지 표시
      }

      // 수정 성공 메시지 Flash Attribute에 담기
      redirectAttributes.addFlashAttribute("status", "게시글이 성공적으로 수정되었습니다!");
      // 수정 후 해당 게시글 상세 페이지로 리다이렉트

      return "redirect:/board/" + id;

    } catch (Exception e) {
      log.error("### 게시글 수정 중 DB 오류 발생", e); // DB 에러 발생 시 로그
      model.addAttribute("board", board); // 입력 데이터 유지
      model.addAttribute("errorMessage", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage()); // 에러 메시지 모델에 담기
      return "board/editForm"; // 수정 폼 화면으로 돌아가 에러 메시지 표시
    }
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    log.info("### [POST] /board/{}/delete - 게시글 삭제 요청", id);

    try {
      int deletedRows = boardDAO.delete(id);
      log.info("### 게시글 삭제 결과: {}건 삭제됨", deletedRows);

      if (deletedRows > 0) {
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
      } else {
        redirectAttributes.addFlashAttribute("error", "삭제할 게시글을 찾을 수 없습니다.");
      }

      return "redirect:/board";

    } catch (Exception e) {
      log.error("### 게시글 삭제 중 오류 발생: {}", e.getMessage(), e);
      redirectAttributes.addFlashAttribute("error", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
      return "redirect:/board/" + id;
    }
  }
  @GetMapping("/search")
  public String search(@RequestParam("keyword") String keyword,
                       Model model) {
    log.info("### [GET] /board/search - 게시글 검색 요청, keyword: {}", keyword);

    List<Board> searchResult = boardDAO.searchByKeyword(keyword);

    model.addAttribute("boards", searchResult); // 검색 결과를 목록으로 전달
    model.addAttribute("keyword", keyword); // 검색 키워드를 뷰에 전달

    return "board/list"; // 같은 목록 화면 재사용
  }

}

