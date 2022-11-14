package io.github.parkjeongwoong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class SimilarityIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long documentId;

    @Column(nullable = false)
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
