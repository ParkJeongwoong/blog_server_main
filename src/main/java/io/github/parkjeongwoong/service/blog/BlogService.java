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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public Long visited(VisitorsSaveRequestDto requestDto) {
        System.out.println("Visitor just visited : " + requestDto.getUrl());
        System.out.println("Visitor's IP address is : " + requestDto.getIp());
        System.out.println("Current Time : " + new Date());

        // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
        if (isRecordable(requestDto.getIp())) {
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

    private Boolean isRecordable(String ip) {
        String[] notRecordableList = {"58.140.57.190" // 공덕 ip
                                 , "222.110.245.239" // 키움증권 ip
                                 , "0:0:0:0:0:0:0:1"}; // local test ip
        return Arrays.stream(notRecordableList).anyMatch(notRecordable -> Objects.equals(notRecordable, ip))
                || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }
}
