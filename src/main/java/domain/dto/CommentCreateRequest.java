package domain.dto;

import domain.entity.Board;
import domain.entity.Comment;
import domain.entity.User;
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
