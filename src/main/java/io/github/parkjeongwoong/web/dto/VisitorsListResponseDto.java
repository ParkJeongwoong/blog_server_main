package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Visitors;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VisitorsListResponseDto {
    private Long id;
    private String url;
    private LocalDateTime visitedDate;

    public VisitorsListResponseDto(Visitors entity) {
        this.id = entity.getId();
        this.url = entity.getUrl();
        this.visitedDate = entity.getCreatedDate();
    }
}
