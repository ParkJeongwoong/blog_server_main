package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.application.blog.dto.ArticleResponseDto;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticleResponseDto> searchByWords(List<String> words);
}
