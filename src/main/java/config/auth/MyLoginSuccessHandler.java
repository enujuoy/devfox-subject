package config.auth;


import domain.entity.User;
import domain.enum_class.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import repository.UserRepository;

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
            pw.println("<script>alert('" + loginUser.getNickname() + "님은 블랙리스트 입니다. 글, 댓글 작성이 불가능합니다.'); location.href='/';</script>");
        } else {
            String prevPage = (String) request.getSession().getAttribute("prevPage");
            if (prevPage != null) {
                pw.println("<script>alert('" + loginUser.getNickname() + "님 반갑습니다!'); location.href='" + prevPage + "';</script>");
            } else {
                pw.println("<script>alert('" + loginUser.getNickname() + "님 반갑습니다!'); location.href='/';</script>");
            }
        }
        pw.flush();
    }
}
