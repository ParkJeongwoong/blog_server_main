package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Visitors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisitorsSaveRequestDto {
    private String url;
    private String ip;
    private String lastPage;
    private Boolean justVisited;
    @Builder
    public VisitorsSaveRequestDto(String url) {
        this.url = url;
    }

    public Visitors toEntity() {
        return Visitors.builder()
                .url(url)
                .ip(ip)
                .lastPage(lastPage)
                .justVisited(justVisited)
                .build();
    }
}
