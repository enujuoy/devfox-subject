package com.study.board.controller;

import com.study.board.domain.dto.CommentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.study.board.service.BoardService;
import com.study.board.service.CommentService;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    @PostMapping("/{boardId}")
    public String addComments(@PathVariable Long boardId, @ModelAttribute CommentCreateRequest req,
                              Authentication auth, Model model) {
        commentService.writeComment(boardId, req, auth.getName());

        model.addAttribute("message", "コメントが追加されました。");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(boardId) + "/" + boardId);
        return "printMessage";
    }

    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long commentId, @ModelAttribute CommentCreateRequest req,
                              Authentication auth, Model model) {
        Long boardId = commentService.editComment(commentId, req.getBody(), auth.getName());
        model.addAttribute("message", boardId == null ? "無効なリクエストです。" : "コメントが修正されました。");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(boardId) + "/" + boardId);
        return "printMessage";
    }

    @GetMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Authentication auth, Model model) {
        Long boardId = commentService.deleteComment(commentId, auth.getName());
        model.addAttribute("message", boardId == null ? "作成者のみ削除可能です。" : "コメントが削除されました。");
        model.addAttribute("nextUrl", "/boards/" + boardService.getCategory(boardId) + "/" + boardId);
        return "printMessage";
    }
}
