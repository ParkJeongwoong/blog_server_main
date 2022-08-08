package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Visitors;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VisitorsListResponseDto {
    private final Long id;
    private final String url;
    private final String ip;
    private final LocalDateTime visitedDate;
    private final String lastPage;

    public VisitorsListResponseDto(Visitors entity) {
        this.id = entity.getId();
        this.url = entity.getUrl();
        this.ip = entity.getIp();
        this.lastPage = entity.getLastPage();
        this.visitedDate = entity.getCreatedDate();
    }
}
