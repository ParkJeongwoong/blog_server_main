package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.InvertedIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {
    List<InvertedIndex> findAllByDocumentId(long documentId);
    List<InvertedIndex> findAllByTerm(String term);
}
