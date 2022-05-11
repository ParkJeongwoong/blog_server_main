package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Article;
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

    @Builder
    MarkdownSaveRequestDto(String title, String content, String date, String fileName) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.fileName = fileName;
    }

    public Article toEntity() {
        return Article.builder()
                .title(title)
                .content(content)
                .date(date)
                .fileName(fileName)
                .build();
    }
}
