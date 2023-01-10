package io.github.parkjeongwoong.application.user.usecase;

import io.github.parkjeongwoong.application.user.dto.UserLoginRequestDto;
import io.github.parkjeongwoong.application.user.dto.UserLoginResponseDto;
import io.github.parkjeongwoong.entity.user.RefreshToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthUsecase {
    UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response);
    boolean logout(HttpServletRequest request, HttpServletResponse response);
    void extendRefreshToken(RefreshToken refreshToken);
}
