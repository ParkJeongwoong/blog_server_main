package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.service.BlogService;
import io.github.parkjeongwoong.application.blog.service.FileService;
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

    private final BlogService blogService;
    private final FileService fileService;

    // Visit
    @PostMapping("/visited")
    public void visited(@RequestBody VisitorsSaveRequestDto requestDto) { blogService.visited(requestDto); }

    @GetMapping("/count-visitors")
    public long count_visitors() {
        return blogService.countVisitors();
    }

    @GetMapping("/history")
    public List<VisitorsListResponseDto> history() {
        return blogService.history();
    }

    @GetMapping("/page-visitors")
    public List<PageVisitorsListResponseDto> count_visitors_page() { return blogService.countVisitors_page(); }

    @GetMapping("/first-visits")
    public List<PageVisitorsListResponseDto> count_visitors_firstPage() { return blogService.countVisitors_firstPage(); }

    // Article
    @PostMapping("/upload")
    public String article_upload(MultipartHttpServletRequest multiRequest, MarkdownSaveRequestDto requestDto, ImageSaveRequestDto imageSaveRequestDto) {
        return fileService.articleUpload(multiRequest, requestDto, imageSaveRequestDto);
    }

    @GetMapping("/articleList")
    public List<ArticleResponseDto> get_article_list() { return blogService.getArticleList(); }

    @GetMapping("/article/{category}/{categoryId}")
    public ArticleResponseDto get_article(@PathVariable("category") String category, @PathVariable("categoryId") Long categoryId) {
        return blogService.getArticle(category, categoryId);
    }

    // Todo - Controller쪽 코드를 Service로 옮기고 개발
    @PutMapping("/article/{articleId}")
    public CommonResponseDto update_article(@PathVariable("articleId") Long articleId) {
        return null;
    }

    @DeleteMapping("/article/{articleId}")
    public CommonResponseDto delete_article(@PathVariable("articleId") Long articleId) { return blogService.deleteArticle(articleId); }

    // Media
    @GetMapping(value = "image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] get_image(@PathVariable("imageName") String imageName) throws IOException { return fileService.getImage(imageName); }
}
