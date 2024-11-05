package com.study.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.study.board.config.auth.MyAccessDeniedHandler;
import com.study.board.config.auth.MyAuthenticationEntryPoint;
import com.study.board.config.auth.MyLoginSuccessHandler;
import com.study.board.config.auth.MyLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import com.study.board.repository.UserRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    private static final String[] anonymousUserUrl = {"/users/login", "/users/join"};
    private static final String[] authenticatedUserUrl = {"/boards/edit", "/boards/delete", "/likes/", "/users/myPage/", "/users/edit", "/users/delete"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(anonymousUserUrl).anonymous()
                        .requestMatchers(authenticatedUserUrl).authenticated()
                        .requestMatchers("/boards/greeting/write").hasAnyAuthority("BRONZE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/boards/greeting").hasAnyAuthority("BRONZE", "ADMIN")
                        .requestMatchers("/boards/free/write").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/boards/free").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                        .requestMatchers("/boards/gold/**").hasAnyAuthority("GOLD", "ADMIN")
                        .requestMatchers("/users/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/comments/**").hasAnyAuthority("BRONZE", "SILVER", "GOLD", "ADMIN")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new MyAccessDeniedHandler(userRepository))
                        .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .failureUrl("/users/login?fail")
                        .successHandler(new MyLoginSuccessHandler(userRepository))
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new MyLogoutSuccessHandler())
                )
                .build();
    }
}
