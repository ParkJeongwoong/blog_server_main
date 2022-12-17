package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.user.dto.UserLoginRequestDto;
import io.github.parkjeongwoong.application.user.dto.UserLoginResponseDto;
import io.github.parkjeongwoong.application.user.dto.UserSignupRequestDto;
import io.github.parkjeongwoong.application.user.service.AuthService;
import io.github.parkjeongwoong.application.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/user-signup")
    public String userSignup(@RequestBody UserSignupRequestDto requestDto) {
        return userService.userSignup(requestDto);
    }

    @GetMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        log.info("로그인 시도 : User ID = {}", requestDto.getUserId());
        return authService.login(requestDto);
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
