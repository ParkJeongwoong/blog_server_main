package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Article;
import lombok.Getter;

@Getter
public class ArticleListResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String date;

    public ArticleListResponseDto(Article entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.date = entity.getDate();
    }
}
