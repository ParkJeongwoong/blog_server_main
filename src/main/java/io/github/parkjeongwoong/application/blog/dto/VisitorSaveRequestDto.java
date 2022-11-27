package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Visitor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Getter
@NoArgsConstructor
public class VisitorSaveRequestDto {
    private String url;
    private String lastPage;
    private String ip;

    @Builder
    public VisitorSaveRequestDto(String url, String lastPage) {
        this.url = url;
        this.lastPage = lastPage;
    }

    private void setIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-FORWARDED-FOR");
        System.out.println("X-FORWARDED-FOR : " + ip);
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr : " + ip);
        }
        this.ip = ip;
    }

    public Visitor toEntity() {
        if (this.ip == null) setIp();
        return Visitor.builder()
                .url(url)
                .lastPage(lastPage)
                .ip(ip)
                .build();
    }
}
