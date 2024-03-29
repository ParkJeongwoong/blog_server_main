package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.postExample.service.PostsService;
import io.github.parkjeongwoong.application.postExample.dto.PostsResponseDto;
import io.github.parkjeongwoong.application.user.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("posts", postsService.findAllDesc());
        String accessToken = jwtTokenProvider.resolveToken(request, "accessToken");
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            model.addAttribute("userId", userId);
        }
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }

    @GetMapping("/blog/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/blog/upload")
    public String upload() { return "article_upload"; }

    @GetMapping("/blog/monitoring")
    public String monitoring() {
        return "monitoring";
    }

    @GetMapping("/blog/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/blog/login")
    public String login() {
        return "login";
    }
}
