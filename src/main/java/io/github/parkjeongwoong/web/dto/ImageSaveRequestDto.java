package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Article;
import io.github.parkjeongwoong.domain.blog.ArticleRepository;
import io.github.parkjeongwoong.domain.blog.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageSaveRequestDto {
    private Article article;
    private String directory;

    @Builder
    ImageSaveRequestDto(Article article, String directory) {
        this.article = article;
        this.directory = directory;
    }

    public Image toEntity() {
        return Image.builder()
                .article(article)
                .directory(directory)
                .build();
    }
}
