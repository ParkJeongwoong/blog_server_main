package io.github.parkjeongwoong.application.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
public class RefreshJwtAuth extends JwtAuth {

    @Column(nullable = false)
    private String userEmail;

    public RefreshJwtAuth(String userId, String userEmail) {
        super(userId);
        this.userEmail = userEmail;
    }

}
