package com.study.board.repository;

import com.study.board.domain.entity.User;
import com.study.board.domain.enum_class.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {  // ID의 타입은 User 엔티티의 ID 필드 타입과 일치해야 합니다.
    Optional<User> findByLoginId(String loginId);
    Page<User> findAllByNicknameContains(String nickname, Pageable pageable);  // PageRequest 대신 Pageable을 사용
    Boolean existsByLoginId(String loginId);
    Boolean existsByNickname(String nickname);
    Long countAllByUserRole(UserRole userRole);
}
