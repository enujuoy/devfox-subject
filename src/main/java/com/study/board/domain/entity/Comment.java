package com.study.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;      // 作成者

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;    // コメントが付いた掲示板

    public void update(String newBody) {
        this.body = newBody;
    }
}
