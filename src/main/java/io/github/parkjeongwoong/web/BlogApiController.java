package io.github.parkjeongwoong.web;

import io.github.parkjeongwoong.service.blog.BlogService;
import io.github.parkjeongwoong.service.blog.FileService;
import io.github.parkjeongwoong.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
@RequestMapping("blog-api")
public class BlogApiController {

    private final BlogService blogService;
    private final FileService fileService;

    @PostMapping("/visited")
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

    @GetMapping("/count-visitors")
    public long countVisitors() {
        return blogService.countVisitors();
    }

    @GetMapping("/history")
    public List<VisitorsListResponseDto> history() {
        return blogService.history();
    }

    @GetMapping("/page-visitors")
    public List<PageVisitorsListResponseDto> countVisitors_page() { return blogService.countVisitors_page(); }

    @GetMapping("/first-visits")
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() { return blogService.countVisitors_firstPage(); }

    @PostMapping("/upload")
    public String article_upload(MultipartHttpServletRequest multiRequest, MarkdownSaveRequestDto requestDto, ImageSaveRequestDto imageSaveRequestDto) {
        ArrayList<String> imageNames;

        try {
            MultipartFile multipartFile = multiRequest.getFile("markdown");
            List<MultipartFile> multipartFile_images = multiRequest.getFiles("images");
            String category = multiRequest.getParameter("category");
            String subCategory = multiRequest.getParameter("subCategory");

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
                    String fileDate = fileName.substring(0,8);
                    if (!fileDate.matches("^[0-9]+$"))
                        return "파일 이름의 첫 8자리는 작성일로 만들어주세요 (ex. 20220731_파일명)";
                    requestDto.setDate(fileName.substring(0, 8));
                }
                requestDto.setFileName(fileName);
                if (category == null || category.length() == 0)
                    return "카테고리를 입력해주세요";

                requestDto.setCategory(category);
                requestDto.setSubCategory(subCategory);

                System.out.println("fileName : " + fileName);
                System.out.println("title : " + title);

                System.out.println("업로드된 이미지 개수 : " + multipartFile_images.size());
                System.out.println("파일의 이미지 개수 : " + fileService.count_image(streamToString));
                if (multipartFile_images.size() != fileService.count_image(streamToString)) {
                    return "첨부한 이미지 개수가 파일의 이미지 개수와 일치하지 않습니다";
                }

//                if (!fileService.check_image(streamToString, imageSaveRequestDto)) {
//                    return "문서의 이미지와 업로드한 이미지가 다릅니다";
//                }

                imageNames = fileService.save_markdown(requestDto);

                imageSaveRequestDto.setArticle_id(Long.valueOf(imageNames.remove(imageNames.size()-1)));
                if (!fileService.save_images(imageSaveRequestDto, multipartFile_images, imageNames)) return "이미지 저장에 실패했습니다";
            } else {
                return "파일을 첨부해주세요";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "등록되었습니다";
    }

    @GetMapping("/get-article-list")
    public List<ArticleResponseDto> getArticleList() { return blogService.getArticleList(); }

    @GetMapping("/get-article/{category}/{categoryId}")
    public ArticleResponseDto getArticle(@PathVariable("category") String category, @PathVariable("categoryId") Long categoryId) {
        return blogService.getArticle(category, categoryId);
    }

    @GetMapping(value = "image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("imageName") String imageName) throws IOException {
        String[] imagePathList = imageName.split("/");
        String imagePath = String.join(File.separator, imagePathList);
        System.out.println(imagePath);

        InputStream imageStream = new FileInputStream(System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "article_images"
                + File.separator + imageName);
        byte[] imageByteArray = StreamUtils.copyToByteArray(imageStream);
        imageStream.close();
        return imageByteArray;
    }
}
