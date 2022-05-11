package io.github.parkjeongwoong.web;

import io.github.parkjeongwoong.service.blog.BlogService;
import io.github.parkjeongwoong.web.dto.MarkdownSaveRequestDto;
import io.github.parkjeongwoong.web.dto.PageVisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/blog-api/visited")
    public void visited(@RequestBody VisitorsSaveRequestDto requestDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-FORWARDED-FOR");
        System.out.println("X-FORWARDED-FOR : " + ip);
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr : " + ip);
        }
        requestDto.setIp(ip);

        if (requestDto.getLastPage() == null) {
            requestDto.setJustVisited(true);
        }
        else {
            requestDto.setJustVisited(false);
        }
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

    @GetMapping("/blog-api/page-visitors")
    public List<PageVisitorsListResponseDto> countVisitors_page() { return blogService.countVisitors_page(); }

    @GetMapping("/blog-api/first-visits")
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() { return blogService.countVisitors_firstPage(); }

    @PostMapping("/blog-api/upload")
    public String article_upload(MultipartHttpServletRequest multiRequest, MarkdownSaveRequestDto requestDto) {
        try {
            MultipartFile multipartFile = multiRequest.getFile("markdown");

            String fileName = multipartFile.getOriginalFilename();
            InputStream file = multipartFile.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
            String streamToString = streamOfString.collect(Collectors.joining("\n"));
            String title = streamToString.split("\n",2)[0].replace("# ","");

            System.out.println("fileName : " + fileName);
            System.out.println("title : " + title);

            requestDto.setTitle(title);
            requestDto.setContent(streamToString);
            requestDto.setDate(fileName.substring(0,8));
            requestDto.setFileName(fileName);

            blogService.upload_markdown(requestDto);
        } catch (Exception e) {
            System.out.println(e);
        }
        return "등록되었습니다.";
    }
}
