package com.study.board.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // メッセージ出力後、ホームにredirect
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.println("<script>alert('ログインしたユーザーのみ可能です！'); location.href='/users/login';</script>");
        pw.flush();
    }
}
