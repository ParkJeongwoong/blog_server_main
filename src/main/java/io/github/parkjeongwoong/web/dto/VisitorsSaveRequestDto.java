package io.github.parkjeongwoong.web.dto;

import io.github.parkjeongwoong.domain.blog.Visitors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VisitorsSaveRequestDto {
    private String url;
    @Builder
    public VisitorsSaveRequestDto(String url) {
        this.url = url;
    }

    public Visitors toEntity() {
        return Visitors.builder()
                .url(url)
                .build();
    }
}
