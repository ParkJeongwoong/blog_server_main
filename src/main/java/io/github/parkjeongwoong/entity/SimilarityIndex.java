package io.github.parkjeongwoong.entity;

import io.github.parkjeongwoong.entity.CompositeKey.SimilarityIndexKey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@IdClass(SimilarityIndexKey.class)
public class SimilarityIndex {
    @Id
    private long documentId;

    @Id
    private long counterDocumentId;

    @Column(nullable = false)
    private long similarityScore = 0;

    @Builder
    public SimilarityIndex(long documentId, long counterDocumentId) {
        this.documentId = documentId;
        this.counterDocumentId = counterDocumentId;
    }

    public void addScore(long score) {this.similarityScore += score;}
}
