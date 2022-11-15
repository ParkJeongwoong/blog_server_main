package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.SimilarityIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SimilarityRepository extends JpaRepository<SimilarityIndex, Long> {
    SimilarityIndex findByDocumentIdAndCounterDocumentId(long documentId, long counterDocumentId);
    List<SimilarityIndex> findTop5ByDocumentIdOrderBySimilarityScoreDesc(long documentId);
}
