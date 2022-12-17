package io.github.parkjeongwoong.application.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponseDto {
    private final boolean result;
    private final String message;
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public UserLoginResponseDto(boolean result, String message, String accessToken, String refreshToken) {
        this.result = result;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
