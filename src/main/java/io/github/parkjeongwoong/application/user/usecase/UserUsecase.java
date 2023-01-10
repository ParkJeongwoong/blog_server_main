package io.github.parkjeongwoong.application.user.usecase;

import io.github.parkjeongwoong.application.user.dto.UserSignupRequestDto;
import io.github.parkjeongwoong.entity.user.UserType;

import javax.servlet.http.HttpServletResponse;

public interface UserUsecase {
    String userSignup(UserSignupRequestDto requestDto, HttpServletResponse response);
    UserType setAdmin(String userId);
}
