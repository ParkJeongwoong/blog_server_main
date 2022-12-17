package io.github.parkjeongwoong.entity.user;

import io.github.parkjeongwoong.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String value;

    @Builder
    public RefreshToken(String userId, String userEmail, String value) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.value = value;
    }
}
