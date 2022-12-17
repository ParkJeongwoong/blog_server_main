package io.github.parkjeongwoong.application.user.dto;

import lombok.Getter;

@Getter
public class UserSignupRequestDto {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
}
