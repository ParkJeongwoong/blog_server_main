package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, String> {
}
