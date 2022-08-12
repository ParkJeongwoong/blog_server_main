package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ArticleUpdateRequestDto {
    private String content;
}
