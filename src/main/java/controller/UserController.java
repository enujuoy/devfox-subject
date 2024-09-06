package controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import repository.UserRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final BoardService boardService;

}
