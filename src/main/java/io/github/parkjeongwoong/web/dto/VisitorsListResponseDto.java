package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Visitors;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VisitorsListResponseDto {
    private Long id;
    private LocalDateTime visitedDate;

    public VisitorsListResponseDto(Visitors entity) {
        this.id = entity.getId();
        this.visitedDate = entity.getCreatedDate();
    }
}
