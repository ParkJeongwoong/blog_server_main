package io.github.parkjeongwoong.application.blog.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendArticleSyncDto {
    private String title;
    private String category;
    private String subCategory;
    private String content;
    private String date;

    @Builder
    public SendArticleSyncDto(String title, String category, String subCategory, String content, String date) {
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.content = content;
        this.date = date;
    }
}
