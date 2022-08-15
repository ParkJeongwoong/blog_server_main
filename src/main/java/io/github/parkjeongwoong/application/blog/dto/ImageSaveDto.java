package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Getter
@NoArgsConstructor
public class ImageSaveDto {
    private Article article;
    private String directory;
    private String rootPath;

    @Builder
    ImageSaveDto(Article article, String directory) {
        this.article = article;
        this.directory = directory;
    }

    public Image toEntity() {
        return Image.builder()
                .article(article)
                .directory(directory)
                .build();
    }

    public void setRootPath() {
        this.rootPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "article_images";
        File folder = new File(rootPath);
        boolean isDirectoryCreated = false;
        if (!folder.exists()) isDirectoryCreated = folder.mkdirs();
        if (!isDirectoryCreated) System.out.println("이미지 저장 폴더를 생성했습니다");
    }

    public String saveImage(MultipartFile imageFile, String imageName) throws IOException {
        File destination = new File(rootPath + File.separator + imageName);
        imageFile.transferTo(destination);
        return destination.getPath();
    }
}
