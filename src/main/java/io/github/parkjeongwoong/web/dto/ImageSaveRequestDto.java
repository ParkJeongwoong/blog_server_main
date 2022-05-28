package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageSaveRequestDto {
    private Long article_id;
    private String directory;

    @Builder
    ImageSaveRequestDto(Long article_id, String directory) {
        this.article_id = article_id;
        this.directory = directory;
    }

    public Image toEntity() {
        return Image.builder()
                .article_id(article_id)
                .directory(directory)
                .build();
    }
}
