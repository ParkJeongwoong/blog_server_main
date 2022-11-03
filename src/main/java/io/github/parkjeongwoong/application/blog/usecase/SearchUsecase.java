package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.entity.Article;

import java.util.List;

public interface SearchUsecase {
    List<ArticleSearchResultDto> searchArticle(String words, long offset);
    void makeInvertedIndex(Article article);
}
