package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarkdownSaveRequestDto {
    private String title;
    private String content;
    private String date;
    private String fileName;
    private String category;
    private String subCategory;
    private Long categoryId;

    @Builder
    MarkdownSaveRequestDto(String title, String content, String date, String fileName, String category, String subCategory) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.fileName = fileName;
        this.category = category;
        this.subCategory = subCategory;
    }

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
