package io.github.parkjeongwoong.application.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponseDto {
    private final boolean result;
    private final String message;

    @Builder
    public UserLoginResponseDto(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
