package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.VisitorRepository;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import io.github.parkjeongwoong.entity.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService implements BlogUsecase {
    private final VisitorRepository visitorRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public void visited(VisitorSaveRequestDto requestDto) {
        Visitor visitor = requestDto.toEntity();
        visitor.setData();
        System.out.println("Visitor just visited : " + visitor.getUrl());
        System.out.println("Visitor's IP address is : " + visitor.getIp());
        System.out.println("Current Time : " + new Date());

        if (isRecordable(visitor.getIp())) return ; // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
        visitorRepository.save(visitor);
    }

    @Transactional
    public long countVisitor() {
        return visitorRepository.count();
    }

    @Transactional(readOnly = true)
    public List<VisitorListResponseDto> history() {
        return visitorRepository.findAllByOrderByIdDesc().stream()
                .map(VisitorListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorListResponseDto> countVisitor_page() {
        return visitorRepository.countVisitor_page().stream()
                .map(PageVisitorListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorListResponseDto> countVisitor_firstPage() {
        return visitorRepository.countVisitor_firstPage().stream()
                .map(PageVisitorListResponseDto::new)
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
                                 , "222.110.245.239"}; // 키움증권 ip
        ArrayList<String> notRecordableList = new ArrayList<>(Arrays.asList(notRecordableArray));
        return notRecordableList.contains(ip) || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }
}
