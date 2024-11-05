package com.study.board.controller;

import com.study.board.domain.dto.BoardCreateRequest;
import com.study.board.domain.dto.BoardDto;
import com.study.board.domain.dto.BoardSearchRequest;
import com.study.board.domain.dto.CommentCreateRequest;
import com.study.board.domain.enum_class.BoardCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.study.board.service.BoardService;
import com.study.board.service.CommentService;
import com.study.board.service.LikeService;
import com.study.board.service.UploadImageService;

import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final UploadImageService uploadImageService;

    @GetMapping("/{category}")
    public String boardListPage(@PathVariable String category, Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @RequestParam(required = false) String sortType,
                                @RequestParam(required = false) String searchType,
                                @RequestParam(required = false) String keyword) {
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "カテゴリが存在しません。");
            model.addAttribute("nextUrl", "/");
            return "printMessage";
        }

        model.addAttribute("notices", boardService.getNotice(boardCategory));

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        if (sortType != null) {
            if (sortType.equals("date")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
            } else if (sortType.equals("like")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("likeCnt").descending());
            } else if (sortType.equals("comment")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("commentCnt").descending());
            }
        }

        model.addAttribute("category", category);
        model.addAttribute("boards", boardService.getBoardList(boardCategory, pageRequest, searchType, keyword));
        model.addAttribute("boardSearchRequest", new BoardSearchRequest(sortType, searchType, keyword));
        return "boards/list";
    }

    @GetMapping("/{category}/write")
    public String boardWritePage(@PathVariable String category, Model model) {
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "カテゴリが存在しません。");
            model.addAttribute("nextUrl", "/");
            return "printMessage";
        }

        model.addAttribute("category", category);
        model.addAttribute("boardCreateRequest", new BoardCreateRequest());
        return "boards/write";
    }

    @PostMapping("/{category}")
    public String boardWrite(@PathVariable String category, @ModelAttribute BoardCreateRequest req,
                             Authentication auth, Model model) throws IOException {
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "カテゴリが存在しません。");
            model.addAttribute("nextUrl", "/");
            return "printMessage";
        }

        Long savedBoardId = boardService.writeBoard(req, boardCategory, auth.getName(), auth);
        if (boardCategory.equals(BoardCategory.GREETING)) {
            model.addAttribute("message", "入会挨拶を書いてSILVERランクに昇格しました！\nこれで自由掲示板に投稿できます！");
        } else {
            model.addAttribute("message", savedBoardId + "番の投稿が登録されました。");
        }
        model.addAttribute("nextUrl", "/boards/" + category + "/" + savedBoardId);
        return "printMessage";
    }

    @GetMapping("/{category}/{boardId}")
    public String boardDetailPage(@PathVariable String category, @PathVariable Long boardId, Model model,
                                  Authentication auth) {
        if (auth != null) {
            model.addAttribute("loginUserLoginId", auth.getName());
            model.addAttribute("likeCheck", likeService.checkLike(auth.getName(), boardId));
        }

        BoardDto boardDto = boardService.getBoard(boardId, category);
        // idに該当する投稿がない、またはカテゴリが一致しない場合
        if (boardDto == null) {
            model.addAttribute("message", "該当する投稿が存在しません。");
            model.addAttribute("nextUrl", "/boards/" + category);
            return "printMessage";
        }

        model.addAttribute("boardDto", boardDto);
        model.addAttribute("category", category);

        model.addAttribute("commentCreateRequest", new CommentCreateRequest());
        model.addAttribute("commentList", commentService.findAll(boardId));
        return "boards/detail";
    }

    @PostMapping("/{category}/{boardId}/edit")
    public String boardEdit(@PathVariable String category, @PathVariable Long boardId,
                            @ModelAttribute BoardDto dto, Model model) throws IOException {
        Long editedBoardId = boardService.editBoard(boardId, category, dto);

        if (editedBoardId == null) {
            model.addAttribute("message", "該当する投稿が存在しません。");
            model.addAttribute("nextUrl", "/boards/" + category);
        } else {
            model.addAttribute("message", editedBoardId + "番の投稿が修正されました。");
            model.addAttribute("nextUrl", "/boards/" + category + "/" + boardId);
        }
        return "printMessage";
    }

    @GetMapping("/{category}/{boardId}/delete")
    public String boardDelete(@PathVariable String category, @PathVariable Long boardId, Model model) throws IOException {
        // "greeting" 카테고리는 삭제할 수 없도록 안내 메시지 반환
        if ("greeting".equalsIgnoreCase(category)) {
            model.addAttribute("message", "入会挨拶は削除できません。");
            model.addAttribute("nextUrl", "/boards/greeting");
            return "printMessage";
        }

        // 게시물 삭제 시도
        Long deletedBoardId = boardService.deleteBoard(boardId, category);

        // 게시물이 존재하지 않거나 삭제할 수 없는 경우 에러 메시지, 삭제된 경우 성공 메시지
        if (deletedBoardId == null) {
            model.addAttribute("message", "該当する投稿が存在しません。");
        } else {
            model.addAttribute("message", deletedBoardId + "番の投稿が削除されました。");
        }

        model.addAttribute("nextUrl", "/boards/" + category);
        return "printMessage";
    }


    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource showImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + uploadImageService.getFullPath(filename));
    }

    @GetMapping("/images/download/{boardId}")
    public ResponseEntity<UrlResource> downloadImage(@PathVariable Long boardId) throws MalformedURLException {
        return uploadImageService.downloadImage(boardId);
    }
}
