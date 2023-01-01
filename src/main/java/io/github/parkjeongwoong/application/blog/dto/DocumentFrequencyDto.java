package io.github.parkjeongwoong.application.blog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DocumentFrequencyDto {
    private double df;
    private long articleCount;

    @Builder
    public DocumentFrequencyDto(Word_SimilarityScoreDto wordSimilarityScoreDto) {
        this.df = wordSimilarityScoreDto.getWord().getDocumentFrequency();
        this.articleCount = wordSimilarityScoreDto.getArticleCount();
    }
}
