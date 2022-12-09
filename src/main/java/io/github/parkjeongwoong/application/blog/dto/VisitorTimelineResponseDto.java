package io.github.parkjeongwoong.application.blog.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class VisitorTimelineResponseDto {
    private final LocalDate date;
    private final int hour;
    private int count;

    public VisitorTimelineResponseDto(LocalDate date, int hour) {
        this.date = date;
        this.hour = hour;
        this.count = 0;
    }

    public void addCount() {
        this.count++;
    }
}
