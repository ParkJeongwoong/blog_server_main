package io.github.parkjeongwoong.service.blog;

import io.github.parkjeongwoong.domain.blog.Article;
import io.github.parkjeongwoong.domain.blog.ArticleRepository;
import io.github.parkjeongwoong.domain.blog.Image;
import io.github.parkjeongwoong.domain.blog.ImageRepository;
import io.github.parkjeongwoong.web.dto.ImageSaveRequestDto;
import io.github.parkjeongwoong.web.dto.MarkdownSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UploadService {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public Long upload_markdown(MarkdownSaveRequestDto requestDto) {
        return articleRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public boolean upload_images(ImageSaveRequestDto requestDto, List<MultipartFile> images, String dirName) throws Exception {
        boolean return_val = false;
        short result = -1;
        try {
            String rootPath = System.getProperty("user.dir") + File.separator + "article_images" + File.separator + dirName;
            File folder = new File(rootPath);
            if (!folder.exists() && folder.mkdirs()) return return_val;

            for (MultipartFile image : images) {
                File destination = new File(rootPath + File.separator + image.getOriginalFilename());
                image.transferTo(destination);
                requestDto.setDirectory(destination.getPath());
                imageRepository.save(requestDto.toEntity());
                result++;
            }
        } catch (Exception e) {
            System.out.println("에러 : " + e.getMessage());
        }

        if (result == images.size() - 1) return_val = true;
        return return_val;
    }

    public Integer check_image(String articleData) {
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(articleData);

        int cnt = 0;
        while (image_in_articleData.find())
            cnt++;

        return cnt;
    }
}
