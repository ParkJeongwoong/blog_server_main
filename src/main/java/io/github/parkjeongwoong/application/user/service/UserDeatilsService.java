package io.github.parkjeongwoong.application.user.service;

import io.github.parkjeongwoong.application.user.repository.RefreshTokenRepository;
import io.github.parkjeongwoong.application.user.repository.UserRepository;
import io.github.parkjeongwoong.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserDeatilsService implements UserDetailsService {
    // Security Config의 AuthenticationManager를 사용하려면 Spring Security에서 제공하는 UserDetailsService를 구현한 클래스가 필요

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(NoSuchElementException::new);
    }

    public String getRefreshTokenById(long refreshTokenId) {
        return refreshTokenRepository.findById(refreshTokenId).orElseThrow(NoSuchElementException::new).getValue();
    }

    public User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
    }
}
