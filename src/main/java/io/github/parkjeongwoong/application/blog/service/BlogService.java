package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.VisitorRepository;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService implements BlogUsecase {
    private final VisitorRepository visitorRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public void visited(VisitorsSaveRequestDto requestDto) {
        // ip 확인 작업 및 방문 데이터 저장
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-FORWARDED-FOR");
        System.out.println("X-FORWARDED-FOR : " + ip);
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr : " + ip);
        }
        requestDto.setIp(ip);
        requestDto.setJustVisited(true);
        if (requestDto.getLastPage() != null)
            requestDto.setJustVisited(false);

        // 방문 등록
        System.out.println("Visitor just visited : " + requestDto.getUrl());
        System.out.println("Visitor's IP address is : " + requestDto.getIp());
        System.out.println("Current Time : " + new Date());

        // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
//        if (isRecordable(requestDto.getIp())) {
//            return ;
//        }
        visitorRepository.save(requestDto.toEntity());
    }

    @Transactional
    public long countVisitors() {
        return visitorRepository.count();
    }

    @Transactional(readOnly = true)
    public List<VisitorsListResponseDto> history() {
        return visitorRepository.findAllByOrderByIdDesc().stream()
                .map(VisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_page() {
        return visitorRepository.countVisitors_page().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() {
        return visitorRepository.countVisitors_firstPage().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseDto> getArticleList() {
        return articleRepository.findAllDesc().stream()
                .map(ArticleResponseDto::new)
                .collect(Collectors.toList());
    }

    public ArticleResponseDto getArticle(String category, Long categoryId) {
        return articleRepository.findByCategoryAndId(category, categoryId);
    }

    private Boolean isRecordable(String ip) {
        String[] notRecordableArray = {"58.140.57.190" // 공덕 ip
                                 , "118.221.44.132" // 양평동 ip1
                                 , "39.115.83.55" // 양평동 ip2
                                 , "222.110.245.239" // 키움증권 ip
                                 , "0:0:0:0:0:0:0:1"}; // local test ip
        ArrayList<String> notRecordableList = new ArrayList<>(Arrays.asList(notRecordableArray));
        return notRecordableList.contains(ip) || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }
}
