package io.github.parkjeongwoong.application.blog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PageVisitorListResponseDto {
    private final String url;
    private final long count;

    public PageVisitorListResponseDto(PageVisitorListResponseDtoInterface entity) {
        this.url = entity.getUrl();
        this.count = entity.getCount();
    }
}
