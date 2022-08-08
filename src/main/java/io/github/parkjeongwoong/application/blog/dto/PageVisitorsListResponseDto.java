package io.github.parkjeongwoong.application.blog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PageVisitorsListResponseDto {
    private final String url;
    private final Long count;

    public PageVisitorsListResponseDto(PageVisitorsListResponseDtoInterface entity) {
        this.url = entity.getUrl();
        this.count = entity.getCount();
    }
}
