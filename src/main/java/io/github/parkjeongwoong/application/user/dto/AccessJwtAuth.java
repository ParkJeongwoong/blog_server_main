package io.github.parkjeongwoong.application.user.dto;

import io.github.parkjeongwoong.entity.user.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class AccessJwtAuth extends JwtAuth {

    @Column(nullable = false)
    private UserType userType;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private long refreshTokenId;

    public AccessJwtAuth(String userId, UserType userType, String userName, long refreshTokenId) {
        super(userId);
        this.userType = userType;
        this.userName = userName;
        this.refreshTokenId = refreshTokenId;
    }

    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        roles.add(this.userType.getKey());
        return roles;
    }

}
