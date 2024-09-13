package config;

import config.auth.MyAccessDeniedHandler;
import config.auth.MyAuthenticationEntryPoint;
import config.auth.MyLoginSuccessHandler;
import config.auth.MyLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import repository.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    // ログインしていないユーザーのみがアクセス可能なURL
    private static final String[] anonymousUserUrl = {"/users/login", "/users/join"};

    // ログインしたユーザーのみがアクセス可能なURL
    private static final String[] authenticatedUserUrl = {"/boards/**/**/edit", "/boards/**/**/delete", "/likes/**", "/users/myPage/**", "/users/edit", "/users/delete"};
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(anonymousUserUrl).anonymous()
                .antMatchers(authenticatedUserUrl).authenticated()
                .antMatchers("/boards/greeting/write").hasAnyAuthority("BRONZE", "ADMIN")
                .antMatchers(HttpMethod.POST, "/boards/greeting").hasAnyAuthority("BRONZE", "ADMIN")
                .antMatchers("/boards/free/write").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                .antMatchers(HttpMethod.POST, "/boards/free").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                .antMatchers("/boards/gold/**").hasAnyAuthority("GOLD", "ADMIN")
                .antMatchers("/users/admin/**").hasAuthority("ADMIN")
                .antMatchers("/comments/**").hasAnyAuthority("BRONZE", "SILVER", "GOLD", "ADMIN")
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new MyAccessDeniedHandler(userRepository))           // 認可失敗
                .authenticationEntryPoint(new MyAuthenticationEntryPoint()) // 認証失敗
                .and()
                //ポームログイン
                .formLogin()
                .loginPage("/users/login")      // ログインページ
                .usernameParameter("loginId")   // ログインに使用するID
                .passwordParameter("password")  // ログインに使用するパスワード
                .failureUrl("/users/login?fail")         // ログイン失敗時にリダイレクトされるURL => 失敗メッセージを表示
                .successHandler(new MyLoginSuccessHandler(userRepository))    // ログイン成功時に実行されるHandler
                .and()
                // 로그아웃
                .logout()
                .logoutUrl("/users/logout")     // ログアウトURL
                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .and()
                .build();
    }

}
