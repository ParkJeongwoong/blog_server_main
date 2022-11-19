package io.github.parkjeongwoong.application.blog.dto;

import lombok.Getter;

@Getter
public class VisitorCountResponseDto {
    private final String ip;
    private final long count;

    public VisitorCountResponseDto(VisitorCountResponseDtoInterface entity) {
        this.ip = entity.getIp();
        this.count = entity.getCount();

    }
}
