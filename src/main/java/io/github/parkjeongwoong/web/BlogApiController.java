package io.github.parkjeongwoong.web;

import io.github.parkjeongwoong.service.blog.BlogService;
import io.github.parkjeongwoong.service.blog.UploadService;
import io.github.parkjeongwoong.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
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
    private final UploadService uploadService;

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
        requestDto.setJustVisited(true);
        if (requestDto.getLastPage() != null)
            requestDto.setJustVisited(false);

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
    public String article_upload(MultipartHttpServletRequest multiRequest, MarkdownSaveRequestDto requestDto, ImageSaveRequestDto imageSaveRequestDto) {
        Long id;

        try {
            MultipartFile multipartFile = multiRequest.getFile("markdown");
            List<MultipartFile> multipartFile_images = multiRequest.getFiles("images");

            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = multipartFile.getOriginalFilename();
                InputStream file = multipartFile.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(file);
                Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
                String streamToString = streamOfString.collect(Collectors.joining("\n"));
                String title = streamToString.split("\n", 2)[0].replace("# ", "");

                requestDto.setTitle(title);
                requestDto.setContent(streamToString);
                if (fileName != null) {
                    requestDto.setDate(fileName.substring(0, 8));
                }
                requestDto.setFileName(fileName);

                System.out.println("fileName : " + fileName);
                System.out.println("title : " + title);

                System.out.println("업로드된 이미지 개수 : " + multipartFile_images.size());
                System.out.println("파일의 이미지 개수 : " + uploadService.check_image(streamToString));
                if (multipartFile_images.size() != uploadService.check_image(streamToString)) {
                    return "첨부한 이미지 개수가 파일의 이미지 개수와 일치하지 않습니다";
                }

                id = uploadService.upload_markdown(requestDto);

                imageSaveRequestDto.setArticle_id(id);
                if (!uploadService.upload_images(imageSaveRequestDto, multipartFile_images, id+"_"+fileName)) return "이미지 저장에 실패했습니다";
            } else {
                return "파일을 첨부해주세요";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "등록되었습니다";
    }
}
