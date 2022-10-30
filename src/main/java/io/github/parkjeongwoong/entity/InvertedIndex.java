package io.github.parkjeongwoong.entity;

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

    @Column
    private String term;

    @Column
    private long documentId;

    @Column
    private long firstPosition;

    @Column
    private long priorityScore;
}
