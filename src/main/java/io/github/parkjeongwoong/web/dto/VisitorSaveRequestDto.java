package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Visitors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VisitorSaveRequestDto {

    public Visitors toEntity() {
        return new Visitors();
    }
}
