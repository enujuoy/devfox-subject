package com.study.board.domain.entity;

import com.study.board.domain.dto.BoardDto;
import com.study.board.domain.enum_class.BoardCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Board extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;

    @Enumerated(EnumType.STRING)
    private BoardCategory category; // カテゴリ

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;      // 作成者

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Like> likes;       // いいね
    private Integer likeCnt;        // いいねの数

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Comment> comments; // コメント
    private Integer commentCnt;     // コメントの数

    @OneToOne(fetch = FetchType.LAZY)
    private UploadImage uploadImage;

    public void update(BoardDto dto) {
        this.title = dto.getTitle();
        this.body = dto.getBody();
    }

    public void likeChange(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }

    public void commentChange(Integer commentCnt) {
        this.commentCnt = commentCnt;
    }

    public void setUploadImage(UploadImage uploadImage) {
        this.uploadImage = uploadImage;
    }
}
