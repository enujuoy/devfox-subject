package domain.entity;


import enum_class.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId; //ログイン時に使用するID
    private String password; //パスワード
    private String nickname;
    private LocalDateTime createdAt; //登録時間
    private Integer receivedLikeCnt; //ユーザーが受け取ったいいね数（本人を除く）

    @Enumerated(EnumType.STRING)
    private UserRole userRole; // 権限
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Board> boards;     // 投稿した記事

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Like> likes;       // ユーザーが押したいいね

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments; // コメント

    public void rankUp(UserRole userRole, Authentication auth) {
        this.userRole = userRole;

        // 現在保存されている Authentication 修正 => 再ログインしなくても権限が修正されるため
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(new SimpleGrantedAuthority(userRole.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void likeChange(Integer receivedLikeCnt) {
        this.receivedLikeCnt = receivedLikeCnt;
        if (this.receivedLikeCnt >= 10 && this.userRole.equals(UserRole.SILVER)) {
            this.userRole = UserRole.GOLD;
        }
    }

    public void edit(String newPassword, String newNickname) {
        this.password = newPassword;
        this.nickname = newNickname;
    }

    public void changeRole() {
        if (userRole.equals(UserRole.BRONZE)) userRole = UserRole.SILVER;
        else if (userRole.equals(UserRole.SILVER)) userRole = UserRole.GOLD;
        else if (userRole.equals(UserRole.GOLD)) userRole = UserRole.BLACKLIST;
        else if (userRole.equals(UserRole.BLACKLIST)) userRole = UserRole.BRONZE;
    }

}
