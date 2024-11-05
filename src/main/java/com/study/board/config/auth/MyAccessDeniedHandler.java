package com.study.board.config.auth;

import com.study.board.domain.entity.User;
import com.study.board.domain.enum_class.UserRole;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import com.study.board.repository.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;

@AllArgsConstructor
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    private final UserRepository userRepository;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = null;
        if (auth != null) {
            loginUser = userRepository.findByLoginId(auth.getName()).get();
        }
        String requestURI = request.getRequestURI();

        // ログインしたユーザーが login または join を試みた場合
        if (requestURI.contains("/users/login") || requestURI.contains("/users/join")) {
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('すでにログインしています！'); location.href='/';</script>");
            pw.flush();
        }
        // ゴールド掲示板には、GOLDおよびADMIN権限を持つユーザーのみがアクセス可能
        else if (requestURI.contains("gold")) {
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('ゴールドランク以上のユーザーのみアクセス可能です！'); location.href='/';</script>");
            pw.flush();
        } else  if (loginUser != null && loginUser.getUserRole().equals(UserRole.BLACKLIST)){
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('ブラックリストに登録されたユーザーは、投稿およびコメントの作成ができません。'); location.href='/';</script>");
            pw.flush();
        }
        // BRONZEランクのユーザーが自由掲示板に投稿しようとした場合
        else if (requestURI.contains("free/write")) {
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('加入挨拶を投稿した後、投稿が可能です！'); location.href='/boards/greeting';</script>");
            pw.flush();
        }
        // SILVERランク以上のユーザーが加入挨拶を投稿しようとした場合。
        else if (requestURI.contains("greeting")) {
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('登録挨拶は一度だけ作成可能です！'); location.href='/boards/greeting';</script>");
            pw.flush();
        }
        // ADMIN権限がないのに管理者ページにアクセスした場合
        else if (requestURI.contains("admin")) {
            // メッセージを表示した後、ホームにリダイレクト
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.println("<script>alert('管理者のみアクセス可能です！'); location.href='/';</script>");
            pw.flush();

        }
    }

}
