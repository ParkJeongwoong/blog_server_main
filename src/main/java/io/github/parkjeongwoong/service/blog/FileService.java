package io.github.parkjeongwoong.service.blog;

import io.github.parkjeongwoong.domain.blog.ArticleRepository;
import io.github.parkjeongwoong.domain.blog.ImageRepository;
import io.github.parkjeongwoong.web.dto.ImageSaveRequestDto;
import io.github.parkjeongwoong.web.dto.MarkdownSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class FileService {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public ArrayList<String> save_markdown(MarkdownSaveRequestDto requestDto) {
        String SERVER_ADDRESS = "https://dvlprjw.p-e.kr";
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(requestDto.getContent());
        String content = requestDto.getContent();
        ArrayList<String> imageNames = new ArrayList<>();

        long imageId = articleRepository.count() + 1;
        while (image_in_articleData.find()) {
            String oldImageDirectory = image_in_articleData.group();
            String[] oldImageDirectoryList = oldImageDirectory.split("/");
            String newImageName = java.lang.System.currentTimeMillis() + "_" + oldImageDirectoryList[oldImageDirectoryList.length-1];
            newImageName = newImageName.substring(0, newImageName.length()-1);
            String newImageDirectory = oldImageDirectory.replace(oldImageDirectory.split("!\\[(.*?)]\\(")[1], SERVER_ADDRESS + "/blog-api/image/" + newImageName + ")");
            imageNames.add(newImageName);
            content = content.replace(oldImageDirectory, newImageDirectory);
            imageId++;
        }
        requestDto.setContent(content);
        imageNames.add(articleRepository.save(requestDto.toEntity()).getId().toString());
        return imageNames;
    }

    @Transactional
    public boolean save_images(ImageSaveRequestDto requestDto, List<MultipartFile> images, ArrayList<String> imageNames) {
        boolean return_val = false;
        short result = -1;
        int imageIdx = 0;
        try {
            String rootPath = System.getProperty("user.dir")
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "resources"
                    + File.separator + "article_images";
            System.out.println("rootPath : " + rootPath);
            File folder = new File(rootPath);
            if (!folder.exists()) folder.mkdirs();

            System.out.println("이미지 저장 시작");
            for (MultipartFile image : images) {
                File destination = new File(rootPath + File.separator + imageNames.get(imageIdx));
                System.out.println("이미지 저장 위치 : " + destination.getPath());
                image.transferTo(destination);
                requestDto.setDirectory(destination.getPath());
                imageRepository.save(requestDto.toEntity());
                result++;
                imageIdx++;
            }
            System.out.println("이미지 저장 완료");
        } catch (Exception e) {
            System.out.println("에러 : " + e.getMessage());
        }

        if (result == images.size() - 1) return_val = true;
        return return_val;
    }

    public Integer count_image(String articleData) {
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(articleData);

        int cnt = 0;
        while (image_in_articleData.find())
            cnt++;

        return cnt;
    }

    // Todo - 파일명 맞는지 확인
    public boolean check_image(String articleData, ImageSaveRequestDto requestDto) {
        return true;
    }
}
