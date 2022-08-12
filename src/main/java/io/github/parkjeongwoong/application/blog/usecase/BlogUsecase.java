package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.*;

import java.util.List;

public interface BlogUsecase {
    void visited(VisitorsSaveRequestDto requestDto);
    long countVisitors();
    List<VisitorsListResponseDto> history();
    List<PageVisitorsListResponseDto> countVisitors_page();
    List<PageVisitorsListResponseDto> countVisitors_firstPage();
    List<ArticleResponseDto> getArticleList();
    ArticleResponseDto getArticle(String category, Long categoryId);
}
