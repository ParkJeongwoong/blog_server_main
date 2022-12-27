package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;

import java.util.List;

public interface RecommendationUsecase {
    List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId);
//    void makeSimilarityIndexList(long offset);
//    void makeSimilarityIndexList(long offset, long endpoint);
//    void saveSimilarArticle(long documentId, long articleCount);
    void saveWord(String term);
    public void updateSimilarity();
}
