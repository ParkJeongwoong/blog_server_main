package io.github.parkjeongwoong.web;

import io.github.parkjeongwoong.service.blog.BlogService;
import io.github.parkjeongwoong.web.dto.PageVisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/blog-api/visited")
    public void visited(@RequestBody VisitorsSaveRequestDto requestDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-FORWARDED-FOR");
        System.out.println("X-FORWARDED-FOR : " + ip);
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr : " + ip);
        }
        requestDto.setIp(ip);

        if (requestDto.getLastPage() == null) {
            requestDto.setJustVisited(true);
        }
        else {
            requestDto.setJustVisited(false);
        }
        blogService.visited(requestDto);
    }

    @GetMapping("/blog-api/count-visitors")
    public long countVisitors() {
        return blogService.countVisitors();
    }

    @GetMapping("/blog-api/history")
    public List<VisitorsListResponseDto> history() {
        return blogService.history();
    }

    @GetMapping("/blog-api/page-visitors")
    public List<PageVisitorsListResponseDto> countVisitors_page() { return blogService.countVisitors_page(); }

    @GetMapping("/blog-api/first-visits")
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() { return blogService.countVisitors_firstPage(); }
}
