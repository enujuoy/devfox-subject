package config.auth;

import domain.entity.User;
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
    // 계정이 만료되었는지 (true: 만료 X)
    @Override
    public boolean isAccountNonExpired() { return true; }

    // 계정이 잠겼는지 (true : 잠김 X)
    @Override
    public boolean isAccountNonLocked() { return true; }

    // 비밀번호가 만료되었는지 (ture: 만료 X)
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // 계정이 활성화(사용 가능)인지 (true: 활성화)
    @Override
    public boolean isEnabled() { return true; }

}
