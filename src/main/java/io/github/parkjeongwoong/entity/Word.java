package io.github.parkjeongwoong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Word {

    @Id
    private String term;

    @Column
    private long documentFrequency;

    @Column
    private boolean isUpdated;

    @Builder
    public Word(String term) {
        this.term = term;
        this.documentFrequency = 0;
        this.isUpdated = true;
    }

    public void addDocumentFrequency() {
        this.documentFrequency++;
        this.isUpdated = true;
    }

    public void subDocumentFrequency() {
        this.documentFrequency--;
        this.isUpdated = true;
    }

    public void updateFinished() {
        this.isUpdated = false;
    }

}
