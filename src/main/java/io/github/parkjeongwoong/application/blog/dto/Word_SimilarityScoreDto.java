package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Word;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Word_SimilarityScoreDto {
    private Word word;
    private long articleCount;

    @Builder
    public Word_SimilarityScoreDto(Word word, long articleCount) {
        this.word = word;
        this.articleCount = articleCount;
    }

}
