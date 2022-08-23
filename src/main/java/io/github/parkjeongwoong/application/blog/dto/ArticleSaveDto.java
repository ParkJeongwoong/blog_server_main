package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSaveDto {
    private String title;
    private long categoryId;
    private String category;
    private String subCategory;
    private String content;
    private String date;
    private String fileName;

    public Article toEntity() {
        return Article.builder()
                .title(title)
                .content(content)
                .date(date)
                .fileName(fileName)
                .category(category)
                .subCategory(subCategory)
                .categoryId(categoryId)
                .build();
    }
}
