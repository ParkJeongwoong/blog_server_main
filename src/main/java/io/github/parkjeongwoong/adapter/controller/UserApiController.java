package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.user.dto.UserLoginRequestDto;
import io.github.parkjeongwoong.application.user.dto.UserLoginResponseDto;
import io.github.parkjeongwoong.application.user.dto.UserSignupRequestDto;
import io.github.parkjeongwoong.application.user.usecase.AuthUsecase;
import io.github.parkjeongwoong.application.user.usecase.UserUsecase;
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

    private final UserUsecase userUsecase;
    private final AuthUsecase authUsecase;

    @PostMapping("/user-signup")
    public String userSignup(@RequestBody UserSignupRequestDto requestDto, HttpServletResponse response) {
        return userUsecase.userSignup(requestDto, response);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        log.info("로그인 시도 : User ID = {}", requestDto.getUserId());
        return authUsecase.login(requestDto, response);
    }
    @PostMapping("/logout")
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        return authUsecase.logout(request, response);
    }

    // 임시
    @PutMapping("/changeAdmin/{userId}")
    public String changeAdmin(@PathVariable("userId") String userId) {
        return userUsecase.setAdmin(userId).getKey();
    }

    @GetMapping("/authtest")
    public String authTest() { return "authtest"; }
    @GetMapping("/admintest")
    public String adminTest() { return "admintest"; }

}
