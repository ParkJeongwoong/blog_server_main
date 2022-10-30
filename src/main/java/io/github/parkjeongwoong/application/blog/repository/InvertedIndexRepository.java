package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.InvertedIndex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {
}
