package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.CompositeKey.SimilarityIndexKey;
import io.github.parkjeongwoong.entity.SimilarityIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SimilarityRepository extends JpaRepository<SimilarityIndex, SimilarityIndexKey> {
    List<SimilarityIndex> findTop5ByDocumentIdOrderBySimilarityScoreDesc(long documentId);

}
