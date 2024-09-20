package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import service.BoardService;
import service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final BoardService boardService;

    @GetMapping(value = {"", "/"})
    public String home(Model model) {
        model.addAttribute("userCntDto", userService.getUserCnt());
        model.addAttribute("boardCntDto", boardService.getBoardCnt());
        return "home";
    }


}
