package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.entity.Article;

public interface SearchUsecase {
    void makeInvertedIndex(Article article);
}
