package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.*;

import java.util.List;

public interface BlogUsecase {
    void visited(VisitorSaveRequestDto requestDto);
    long countVisitor();
    List<VisitorListResponseDto> history();
    List<PageVisitorListResponseDto> countVisitor_page();
    List<PageVisitorListResponseDto> countVisitor_firstPage();
    List<ArticleResponseDto> getArticleList();
    ArticleResponseDto getArticle(String category, Long categoryId);
}
