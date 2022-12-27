package io.github.parkjeongwoong.entity;

import io.github.parkjeongwoong.entity.CompositeKey.InvertedIndexKey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@IdClass(InvertedIndexKey.class)
public class InvertedIndex {

    @Id
    private long documentId;

    @Id
    @Column(length = 20)
    private String term;

    @Column
    private long firstPosition;

    @Column(nullable = false)
    private long termFrequency;

    @Column(nullable = false)
    private double priorityScore;

    @Builder
    public InvertedIndex(String term, long documentId, long firstPosition, String textType) {
        this.term = term;
        this.documentId = documentId;
        this.firstPosition = firstPosition;
        this.termFrequency = 0;
        this.priorityScore = 0;
        addTermFrequency(textType);
    }

    public void addTermFrequency(String textType) {
        switch (textType) {
            case "title":
                this.termFrequency += 20;
            case "content":
                this.termFrequency += 1;
        }
        this.priorityScore = termFrequency;
    }

    public double TFIDF(double df, long totalArticleNumber) {
        double tf = this.termFrequency;
        double idf = Math.log(totalArticleNumber / df);
        this.priorityScore = tf * idf;
        return this.priorityScore;
    }
}
