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
    private long priorityScore;

    @Builder
    public InvertedIndex(String term, long documentId, long firstPosition, String textType) {
        this.term = term;
        this.documentId = documentId;
        this.firstPosition = firstPosition;
        this.priorityScore = 0;
        addPriorityScore(textType);
    }

    public void addPriorityScore(String textType) {
        switch (textType) {
            case "title":
                this.priorityScore += 20;
            case "content":
                this.priorityScore += 1;
        }
    }
}
