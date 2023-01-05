package io.github.parkjeongwoong.application.user.service;

import io.github.parkjeongwoong.application.user.repository.RefreshTokenRepository;
import io.github.parkjeongwoong.application.user.repository.UserRepository;
import io.github.parkjeongwoong.entity.user.RefreshToken;
import io.github.parkjeongwoong.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    // Security Config의 AuthenticationManager를 사용하려면 Spring Security에서 제공하는 UserDetailsService를 구현한 클래스가 필요

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        try {
            return userRepository.findByUsername(userName).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            log.error("존재하지 않는 사용자입니다.");
            return null;
        }
    }

    public RefreshToken getRefreshTokenById(long refreshTokenId) {
        try {
            return refreshTokenRepository.findById(refreshTokenId).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            log.error("존재하지 않는 사용자입니다.");
            return null;
        }
    }

    public User getUser(String userId) {
        try {
            return userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            log.error("존재하지 않는 사용자입니다.");
            return null;
        }
    }
}
