package com.study.board.config.auth;


import com.study.board.domain.entity.User;
import com.study.board.domain.enum_class.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.study.board.repository.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;
@AllArgsConstructor
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // セッションの維持時間　＝　3600秒。
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(3600);

        User loginUser = userRepository.findByLoginId(authentication.getName()).get();

        // 成功時にメッセージを表示した後、ホーム画面にリダイレクト
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        if (loginUser.getUserRole().equals(UserRole.BLACKLIST)) {
            pw.println("<script>alert('" + loginUser.getNickname() + "さんはブラックリストに登録されています。投稿やコメントの作成はできません。'); location.href='/';</script>");
        } else {
            String prevPage = (String) request.getSession().getAttribute("prevPage");
            if (prevPage != null) {
                pw.println("<script>alert('" + loginUser.getNickname() + "さん、ようこそ！'); location.href='" + prevPage + "';</script>");
            } else {
                pw.println("<script>alert('" + loginUser.getNickname() + "さん、ようこそ！'); location.href='/';</script>");
            }
        }

        pw.flush();
    }
}
