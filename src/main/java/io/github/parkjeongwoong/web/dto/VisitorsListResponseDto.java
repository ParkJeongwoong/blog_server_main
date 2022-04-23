package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Visitors;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VisitorsListResponseDto {
    private Long id;
    private String url;
    private String ip;
    private LocalDateTime visitedDate;
    private String lastPage;

    public VisitorsListResponseDto(Visitors entity) {
        this.id = entity.getId();
        this.url = entity.getUrl();
        this.ip = entity.getIp();
        this.lastPage = entity.getLastPage();
        this.visitedDate = entity.getCreatedDate();
    }
}
