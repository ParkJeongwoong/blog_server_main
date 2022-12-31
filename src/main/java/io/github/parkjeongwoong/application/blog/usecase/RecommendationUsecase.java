package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;

import java.util.List;

public interface RecommendationUsecase {
    List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId);
    void saveWord(String term);
    void deleteWordByDocumentId_similarityUpdate(long documentId);
    void deleteWordByDocumentId_wordEffectOnly(long documentId);
    void createSimilarityIndex(long documentId);
    void updateSimilarity();
    void updateAllSimilarity();
    void resetAllSimilarity();
}
