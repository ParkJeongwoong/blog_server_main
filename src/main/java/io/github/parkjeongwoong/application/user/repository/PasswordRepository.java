package io.github.parkjeongwoong.application.user.repository;

import io.github.parkjeongwoong.entity.user.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
