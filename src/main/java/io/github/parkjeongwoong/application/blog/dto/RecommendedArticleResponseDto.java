package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendedArticleResponseDto {
    private String title;
    private long categoryId;
    private String category;
    private String subCategory;
    private String date;

    public RecommendedArticleResponseDto(Article entity) {
        this.title = entity.getTitle();
        this.categoryId = entity.getCategoryId();
        this.category = entity.getCategory();
        this.subCategory = entity.getSubCategory();
        this.date = entity.getDate();
    }
}
