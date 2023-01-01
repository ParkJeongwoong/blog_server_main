package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;
import io.github.parkjeongwoong.application.blog.dto.Word_SimilarityScoreDto;

import java.util.List;

public interface RecommendationUsecase {
    List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId);
    void createSimilarityIndex(long documentId);
    void updateSimilarity();
    void updateAllSimilarity();
    void resetAllSimilarity();
    void updateSimilarityByWord(Word_SimilarityScoreDto wordSimilarityScoreDto);
}
