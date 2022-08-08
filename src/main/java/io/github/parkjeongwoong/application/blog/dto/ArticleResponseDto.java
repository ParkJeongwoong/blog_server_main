package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;

@Getter
public class ArticleResponseDto {
    private final Long id;
    private final String title;
    private final String category;
    private final String subCategory;
    private final String content;
    private final String date;

    public ArticleResponseDto(Article entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.category = entity.getCategory();
        this.subCategory = entity.getSubCategory();
        this.content = entity.getContent();
        this.date = entity.getDate();
    }
}
