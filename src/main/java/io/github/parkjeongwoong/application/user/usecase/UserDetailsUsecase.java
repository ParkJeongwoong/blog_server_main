package io.github.parkjeongwoong.application.user.usecase;

import io.github.parkjeongwoong.entity.user.RefreshToken;
import io.github.parkjeongwoong.entity.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsUsecase extends org.springframework.security.core.userdetails.UserDetailsService {
    UserDetails loadUserByUsername(String userName);
    RefreshToken getRefreshTokenById(long refreshTokenId);
    User getUser(String userId);
}
