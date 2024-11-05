package com.study.board.domain.dto;

import com.study.board.domain.entity.Board;
import com.study.board.domain.entity.Comment;
import com.study.board.domain.entity.User;
import lombok.Data;

@Data
public class CommentCreateRequest {

    private String body;

    public Comment toEntity(Board board, User user){
        return Comment.builder()
                .user(user)
                .board(board)
                .body(body)
                .build();
    }

}
