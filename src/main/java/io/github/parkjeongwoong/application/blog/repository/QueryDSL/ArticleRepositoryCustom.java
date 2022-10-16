package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.application.blog.dto.ArticleResponseDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticleSearchResultDto> searchByWords(List<String> words, Long offset);
}
