package com.study.board.repository;

import com.study.board.domain.entity.UploadImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UploadImageRepository extends JpaRepository<UploadImage, Long> {
}
