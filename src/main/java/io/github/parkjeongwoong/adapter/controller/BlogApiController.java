package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import io.github.parkjeongwoong.application.blog.usecase.FileUsecase;
import io.github.parkjeongwoong.application.blog.usecase.RecommendationUsecase;
import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import io.github.parkjeongwoong.entity.ServerState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final SearchUsecase searchUsecase;
    private final RecommendationUsecase recommendationUsecase;

    // Visit
    @PostMapping("/visited")
    public void visited(@RequestBody VisitorSaveRequestDto requestDto) { blogUsecase.visited(requestDto); }

    @GetMapping("/history")
    public List<VisitorResponseDto> history() { return blogUsecase.history(); }

    @GetMapping("/count-visitor")
    public long count_visitor() {
        return blogUsecase.countVisitor();
    }

    @GetMapping("/page-visitor")
    public List<PageVisitorResponseDto> count_visitor_page() { return blogUsecase.countVisitor_page(); }

    @GetMapping("/first-visits")
    public List<PageVisitorResponseDto> count_visitor_firstPage() { return blogUsecase.countVisitor_firstPage(); }

    @GetMapping("/daily-visitor")
    public List<DailyVisitorResponseDto> count_daily_visitor() { return blogUsecase.countDailyVisitor(); }

    @GetMapping("/visitor-rank")
    public List<VisitorCountResponseDto> count_visitor_rank() { return blogUsecase.countVisitorRank(); }

    @GetMapping("/visitor-rank/{startDate}/{endDate}")
    public List<VisitorCountResponseDto> count_visitor_rank_date(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return blogUsecase.countVisitorRank_date(startDate, endDate);
    }

    @GetMapping("/visitor-in-hours/{startDate}/{endDate}")
    public List<VisitorInHoursResponseDto> get_visitor_in_hours(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate)
        throws Exception {
        return blogUsecase.getVisitorInHours(startDate, endDate);
    }

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

    // Search
    @PutMapping("/search/make-inverted-index")
    public void make_inverted_index() { searchUsecase.invertedIndexProcess(); }

    @GetMapping("/search/article/{word}")
    public List<ArticleSearchResultDto> search_article(@PathVariable("word") String words) { return searchUsecase.searchArticle(words, 0); }
    @GetMapping("/search/article/{word}/{offset}")
    public List<ArticleSearchResultDto> search_article_offset(@PathVariable("word") String words, @PathVariable("offset") Long offset) {
        return searchUsecase.searchArticle(words, offset);
    }

    @PutMapping("/article/{articleId}")
    public CommonResponseDto update_article(@PathVariable("articleId") long articleId, @RequestBody ArticleUpdateRequestDto requestDto) {
        return fileUsecase.updateArticle_string(articleId, requestDto);
    }

    @DeleteMapping("/article/{articleId}")
    public CommonResponseDto delete_article(@PathVariable("articleId") long articleId) { return fileUsecase.deleteArticle(articleId); }

    // Media
    @GetMapping(value = "image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] get_image(@PathVariable("imageName") String imageName) throws IOException { return blogUsecase.getImage(imageName); }

    // Recommendation
    @PutMapping("/recommend/make-similarity-index")
    public void make_similarity_index() { recommendationUsecase.makeSimilarityIndex(0); }
    @PutMapping("/recommend/make-similarity-index/{offset}")
    public void make_similarity_index_offset(@PathVariable("offset") Long offset) { recommendationUsecase.makeSimilarityIndex(offset); }
    @PutMapping("/recommend/make-similarity-index/{offset}/{endpoint}")
    public void make_similarity_index_offset(@PathVariable("offset") Long offset, @PathVariable("endpoint") Long endpoint) { recommendationUsecase.makeSimilarityIndex(offset, endpoint); }

    @GetMapping(value = "/recommend/get-5-similar-article/{articleId}")
    public List<RecommendedArticleResponseDto> get_5_similar_article(@PathVariable("articleId") long articleId) {
        return recommendationUsecase.get5SimilarArticle(articleId);
    }

    // 전역 변수 TEST
    @Autowired
    private ServerState serverState;
    @GetMapping("/checkUpdate")
    public long checkUpdate() { return serverState.checkArticleUpdate(); }
    @GetMapping("/setUpdate")
    public void setUpdate() { serverState.articleIsUpdated(); }
    @GetMapping("/finishUpdate")
    public void finishUpdate() { serverState.postArticleUpdateProcessIsDone(); }

}
