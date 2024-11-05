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
public class UploadImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;    // 元のファイル名
    private String savedFilename;        // サーバーに保存されたファイル名

    @OneToOne(mappedBy = "uploadImage", fetch = FetchType.LAZY)
    private Board board;
}
