package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.SimilarityIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimilarityRepository extends JpaRepository<SimilarityIndex, Long> {
    SimilarityIndex findByDocumentIdAndCounterDocumentId(long documentId, long counterDocumentId);
}
