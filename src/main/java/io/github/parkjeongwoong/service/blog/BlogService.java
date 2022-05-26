package io.github.parkjeongwoong.service.blog;

import io.github.parkjeongwoong.domain.blog.ArticleRepository;
import io.github.parkjeongwoong.domain.blog.BlogRepository;
import io.github.parkjeongwoong.web.dto.MarkdownSaveRequestDto;
import io.github.parkjeongwoong.web.dto.PageVisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public Long visited(VisitorsSaveRequestDto requestDto) {
        System.out.println("Visitor just visited : " + requestDto.getUrl());
        System.out.println("Visitor's IP address is : " + requestDto.getIp());
        System.out.println("Current Time : " + new Date().toString());
        if (requestDto.getIp().substring(0,6) == "66.249" || requestDto.getIp() == "58.140.57.190") { // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
            return -1L;
        }
        return blogRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public long countVisitors() {
        return blogRepository.findAllDesc().stream().count();
    }

    @Transactional(readOnly = true)
    public List<VisitorsListResponseDto> history() {
        return blogRepository.findAllDesc().stream()
                .map(VisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_page() {
        return blogRepository.countVisitors_page().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() {
        return blogRepository.countVisitors_firstPage().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public String upload_markdown(MarkdownSaveRequestDto requestDto) {
        articleRepository.save(requestDto.toEntity());
        return "done";
    }
}
