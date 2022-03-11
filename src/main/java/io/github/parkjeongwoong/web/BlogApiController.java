package io.github.parkjeongwoong.web;

import io.github.parkjeongwoong.config.auth.blog.BlogService;
import io.github.parkjeongwoong.web.dto.HelloResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/blog-api/visited")
    public void visited(@RequestBody VisitorsSaveRequestDto requestDto) {
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
}
