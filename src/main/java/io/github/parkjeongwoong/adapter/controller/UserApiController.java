package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.user.dto.UserLoginRequestDto;
import io.github.parkjeongwoong.application.user.dto.UserLoginResponseDto;
import io.github.parkjeongwoong.application.user.dto.UserSignupRequestDto;
import io.github.parkjeongwoong.application.user.service.AuthService;
import io.github.parkjeongwoong.application.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user-api")
public class UserApiController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/user-signup")
    public String userSignup(@RequestBody UserSignupRequestDto requestDto, HttpServletResponse response) {
        return userService.userSignup(requestDto, response);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        log.info("로그인 시도 : User ID = {}", requestDto.getUserId());
        return authService.login(requestDto, response);
    }
    @PostMapping("/logout")
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }

    // 임시
    @PutMapping("/changeAdmin/{userId}")
    public String changeAdmin(@PathVariable("userId") String userId) {
        return userService.setAdmin(userId).getKey();
    }

    @GetMapping("/authtest")
    public String authTest() { return "authtest"; }
    @GetMapping("/admintest")
    public String adminTest() { return "admintest"; }

}
