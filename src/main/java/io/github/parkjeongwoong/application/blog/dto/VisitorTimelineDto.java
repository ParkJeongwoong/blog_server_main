package io.github.parkjeongwoong.application.blog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.github.parkjeongwoong.entity.Visitor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class VisitorTimelineDto {
    private final LocalDateTime visitedDate;

    public VisitorTimelineDto(Visitor entity) {
        this.visitedDate = entity.getCreatedDate();
    }
}
