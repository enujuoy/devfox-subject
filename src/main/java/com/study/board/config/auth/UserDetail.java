package com.study.board.config.auth;

import com.study.board.domain.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class UserDetail implements UserDetails {
    private User user;

    public UserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> {
           return user.getUserRole().toString();
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }
    // アカウントが有効かどうかを確認 (true: 有効、false: 期限切れ)
    @Override
    public boolean isAccountNonExpired() { return true; }

    // アカウントがロックされているかどうかを確認（true: ロックされていない、false: ロックされている）。
    @Override
    public boolean isAccountNonLocked() { return true; }

    // パスワードが有効かどうかを確認（true: 有効、false: 期限切れ）。
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // アカウントがアクティブ（使用可能）かどうかを確認（true: アクティブ、false: 非アクティブ）。
    @Override
    public boolean isEnabled() { return true; }

}
