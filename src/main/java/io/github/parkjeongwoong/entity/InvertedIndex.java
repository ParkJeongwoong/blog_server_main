package io.github.parkjeongwoong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class InvertedIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String term;

    @Column(nullable = false)
    private long documentId;

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

    public void TFIDF(double df, long totalArticleNumber) {
        double tf = this.termFrequency;
        double idf = Math.log(totalArticleNumber / df);
        this.priorityScore = tf * idf;
    }
}
