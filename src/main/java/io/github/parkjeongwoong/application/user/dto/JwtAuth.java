package io.github.parkjeongwoong.application.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
public class JwtAuth {

    @Id
    private String userId;

    public JwtAuth(String userId) {
        this.userId = userId;
    }

}
