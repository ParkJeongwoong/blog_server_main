package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.entity.SimilarityIndex;

import java.util.List;

public interface RecommendationUsecase {
    List<SimilarityIndex> get5SimilarArticle(long documentId);
    void makeSimilarityIndex();
}
