package io.github.parkjeongwoong.web.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PageVisitorsListResponseDto {
    private String url;
    private Long count;

    public PageVisitorsListResponseDto(PageVisitorsListResponseDtoInterface entity) {
        this.url = entity.getUrl();
        this.count = entity.getCount();
    }
}
