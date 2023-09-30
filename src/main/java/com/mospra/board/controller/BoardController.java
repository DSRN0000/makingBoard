package com.mospra.board.controller;

import com.mospra.board.entity.Board;
import com.mospra.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @GetMapping("/board/write")  //localhost8080/board/write
    public String boardWriteForm(){

        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword){ // Model : 데이터를 담아서 우리가 보이는 페이지로 보내줌

        Page<Board> list = null;

        if (searchKeyword == null){
            list = boardService.boardList(pageable);
        }else {
            list = boardService.boardSearchList(searchKeyword,pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage -4 ,1);
        int endPage = Math.min(nowPage + 5 , list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return  "boardlist";
    }

    @GetMapping("/board/view") //localhost:8080/board/view?id=1 -> 1이 id로 들어감 그리고 게시글 불러옴(파라미터,get방식이라고 함)
    public String boardView(Model model, Integer id){

        model.addAttribute("board",boardService.boardView(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){

        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}") //패스베리어블 방법 : {id} 부분이 @PathVariable("id") 에 인식이 되고 Integer형태의 id로 들어옴
    // ?뒤로 넣는것이 아니라 /뒤로 깔끔하게 들어감
    public String boardModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("board",boardService.boardView(id));

        return "boardmodify";
    }
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception { //boardwrite의 type="file" 의 name="file"과 MultipartFile 의 file을 일치시켜줘야 한다.

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle()); //기존 객체
        boardTemp.setContent(board.getContent()); //새로 쓴 객체 덮어씌움

        boardService.write(boardTemp, file); //저장 -> 업데이트 완료

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }
}

