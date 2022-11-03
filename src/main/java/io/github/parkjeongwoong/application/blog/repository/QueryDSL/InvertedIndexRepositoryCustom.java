package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;

import java.util.List;

public interface InvertedIndexRepositoryCustom {
    List<ArticleSearchResultDto> searchArticle(List<String> words, Long offset);
}
