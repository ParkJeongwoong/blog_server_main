package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageSaveDto {
    private Article article;
    private String directory;

    @Builder
    ImageSaveDto(Article article, String directory) {
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
