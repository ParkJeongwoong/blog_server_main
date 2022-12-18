package io.github.parkjeongwoong.entity.user;

import io.github.parkjeongwoong.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    private String userId;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @OneToOne(mappedBy = "user")
    private Password EncryptedPassword;

    @Column
    private String password; // UserDetails용

    @Builder
    public User(String userId, String username, String email, UserType userType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    public User update(String name, String email) {
        this.username = name;
        this.email = email;

        return this;
    }

    public UserType userTypeChange(UserType userType) {
        this.userType = userType;
        return this.userType;
    }

    // JWT 토큰 인증을 위한 UsernamePasswordAuthenticationToken 생성 시 필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.userType.getKey()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
