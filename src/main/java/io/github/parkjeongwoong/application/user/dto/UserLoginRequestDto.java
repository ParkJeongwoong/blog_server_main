package io.github.parkjeongwoong.application.user.dto;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String userId;
    private String password;
}
