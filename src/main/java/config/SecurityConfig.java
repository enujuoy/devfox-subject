package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import config.auth.MyAccessDeniedHandler;
import config.auth.MyAuthenticationEntryPoint;
import config.auth.MyLoginSuccessHandler;
import config.auth.MyLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "repository")  // UserRepository가 있는 패키지
@ComponentScan(basePackages = "repository") // 필요한 경우 추가로 스캔할 패키지 지정
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    private static final String[] anonymousUserUrl = {"/users/login", "/users/join"};
    private static final String[] authenticatedUserUrl = {"/boards/**/**/edit", "/boards/**/**/delete", "/likes/**", "/users/myPage/**", "/users/edit", "/users/delete"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 설정 비활성화
                .cors(cors -> cors.and()) // CORS 설정 추가 (필요한 경우)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(anonymousUserUrl).anonymous()
                        .requestMatchers(authenticatedUserUrl).authenticated()
                        .requestMatchers("/boards/greeting/write").hasAnyAuthority("BRONZE", "ADMIN")
                        .requestMatchers("/boards/free/write").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/boards/greeting").hasAnyAuthority("BRONZE", "ADMIN")
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
                );

        return http.build();
    }
}
