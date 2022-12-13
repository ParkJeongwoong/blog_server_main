package io.github.parkjeongwoong.entity.user;

import io.github.parkjeongwoong.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @OneToOne(mappedBy = "user")
    private Password password;

    @Builder
    public User(String userId, String name, String email, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public User update(String name, String email) {
        this.name = name;
        this.email = email;

        return this;
    }

}
