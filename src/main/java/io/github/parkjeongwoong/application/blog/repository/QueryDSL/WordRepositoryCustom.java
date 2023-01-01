package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.application.blog.dto.Word_SimilarityScoreDto;

import java.util.List;

public interface WordRepositoryCustom {
    List<Word_SimilarityScoreDto> findAllByIsUpdatedTrue();
    List<Word_SimilarityScoreDto> findAllByIdWithArticleCount(List<String> termList);
    List<Word_SimilarityScoreDto> findAllWithArticleCount();
}
