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
    private double similarityScore = 0;

    @Builder
    public SimilarityIndex(long documentId, long counterDocumentId) {
        this.documentId = documentId;
        this.counterDocumentId = counterDocumentId;
    }

    public void addScore(double score) {this.similarityScore += score;System.out.println(this.similarityScore);}
    public void subScore(double score) {this.similarityScore -= score;}
}
