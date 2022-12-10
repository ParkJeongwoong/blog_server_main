package io.github.parkjeongwoong.application.blog.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class VisitorInHoursResponseDto {
    private final LocalDate date;
    private final int hour;
    private int count;

    public VisitorInHoursResponseDto(LocalDate date, int hour) {
        this.date = date;
        this.hour = hour;
        this.count = 0;
    }

    public void addCount() {
        this.count++;
    }
}
