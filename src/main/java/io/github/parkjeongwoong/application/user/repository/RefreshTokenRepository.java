package io.github.parkjeongwoong.application.user.repository;

import io.github.parkjeongwoong.entity.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findRefreshTokenByIdAndUserId(long id, String userId);
}
