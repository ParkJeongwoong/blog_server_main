package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import io.github.parkjeongwoong.application.blog.usecase.FileUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("blog-api")
public class BlogApiController {

    private final BlogUsecase blogUsecase;
    private final FileUsecase fileUsecase;

    // Visit
    @PostMapping("/visited")
    public void visited(@RequestBody VisitorsSaveRequestDto requestDto) { blogUsecase.visited(requestDto); }

    @GetMapping("/count-visitors")
    public long count_visitors() {
        return blogUsecase.countVisitors();
    }

    @GetMapping("/history")
    public List<VisitorsListResponseDto> history() {
        return blogUsecase.history();
    }

    @GetMapping("/page-visitors")
    public List<PageVisitorsListResponseDto> count_visitors_page() { return blogUsecase.countVisitors_page(); }

    @GetMapping("/first-visits")
    public List<PageVisitorsListResponseDto> count_visitors_firstPage() { return blogUsecase.countVisitors_firstPage(); }

    // Article
    @PostMapping("/upload")
    public CommonResponseDto article_upload(MultipartHttpServletRequest multiRequest) {
        return fileUsecase.saveArticle(multiRequest);
    }

    @GetMapping("/articleList")
    public List<ArticleResponseDto> get_article_list() { return blogUsecase.getArticleList(); }

    @GetMapping("/article/{category}/{categoryId}")
    public ArticleResponseDto get_article(@PathVariable("category") String category, @PathVariable("categoryId") Long categoryId) {
        return blogUsecase.getArticle(category, categoryId);
    }

    @PutMapping("/article/{articleId}")
    public CommonResponseDto update_article(@PathVariable("articleId") Long articleId, @RequestBody ArticleUpdateRequestDto requestDto) {
        return fileUsecase.updateArticle(articleId, requestDto);
    }

    @DeleteMapping("/article/{articleId}")
    public CommonResponseDto delete_article(@PathVariable("articleId") Long articleId) { return fileUsecase.deleteArticle(articleId); }

    // Media
    @GetMapping(value = "image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] get_image(@PathVariable("imageName") String imageName) throws IOException { return fileUsecase.getImage(imageName); }
}
