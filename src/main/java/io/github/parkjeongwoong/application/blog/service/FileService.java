package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleUpdateRequestDto;
import io.github.parkjeongwoong.application.blog.dto.CommonResponseDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.usecase.FileUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.application.blog.repository.ImageRepository;
import io.github.parkjeongwoong.application.blog.dto.ImageSaveDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class FileService implements FileUsecase {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public CommonResponseDto saveArticle(MultipartHttpServletRequest multiRequest) {
        try {
            MultipartFile multipartFile = multiRequest.getFile("markdown");
            List<MultipartFile> imageFiles = multiRequest.getFiles("images");

            if (multipartFile != null && !multipartFile.isEmpty()) {
                ArticleSaveDto articleSaveDto = new ArticleSaveDto();

                String category = multiRequest.getParameter("category");
                String subCategory = multiRequest.getParameter("subCategory");
                String fileName = multipartFile.getOriginalFilename();
                InputStream file = multipartFile.getInputStream();
                String content = getContent(file);
                String title = content.split("\n", 2)[0].replace("# ", "");

                if (fileName == null)
                    return new CommonResponseDto("Save Article", "Failed", "파일명을 확인할 수 없습니다");
                String fileDate = fileName.substring(0,8);
                if (!fileDate.matches("^[0-9]+$"))
                    return new CommonResponseDto("Save Article", "Failed", "파일 이름의 첫 8자리는 작성일로 만들어주세요 (ex. 20220731_파일명)");
                if (category == null || category.length() == 0)
                    return new CommonResponseDto("Save Article", "Failed", "카테고리를 입력해주세요");

                long categoryId = articleRepository.countCategory(category);

                articleSaveDto.setCategoryId(categoryId);
                articleSaveDto.setTitle(title);
                articleSaveDto.setContent(content);
                articleSaveDto.setDate(fileName.substring(0, 8));
                articleSaveDto.setFileName(fileName);
                articleSaveDto.setCategory(category);
                articleSaveDto.setSubCategory(subCategory);

                System.out.println("fileName : " + fileName);
                System.out.println("title : " + title);

                System.out.println("업로드된 이미지 개수 : " + imageFiles.size());
                System.out.println("파일의 이미지 개수 : " + count_image(content));
                if (imageFiles.size() != count_image(content)) {
                    return new CommonResponseDto("Save Article", "Failed", "첨부한 이미지 개수가 파일의 이미지 개수와 일치하지 않습니다");
                }

                // (이미지 이름 일치여부 확인) 이 부분은 save_images로 (이거 때매 return 값을 boolean에서 string으로 변경)
//                if (!fileService.check_image(streamToString, imageSaveRequestDto)) {
//                    return "문서의 이미지와 업로드한 이미지가 다릅니다";
//                }

                return save_markdown(articleSaveDto, imageFiles);
            } else {
                return new CommonResponseDto("Save Article", "Failed", "파일을 첨부해주세요");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResponseDto("Save Article", "Failed", "파일 저장 중 에러가 발생했습니다");
        }
    }

    @Transactional
    public CommonResponseDto updateArticle(Long articleId, ArticleUpdateRequestDto requestDto) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = "+ articleId));
        article.update(requestDto.getContent());
        return new CommonResponseDto("Update Article", "Success", "게시글을 성공적으로 수정했습니다.");
    }

    @Transactional
    public CommonResponseDto updateArticle_markdown(Long articleId, MultipartHttpServletRequest multiRequest) {
        return null;
    }

    public CommonResponseDto deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = "+ articleId));
        articleRepository.delete(article);
        return new CommonResponseDto("Delete Article", "Success", "게시글을 성공적으로 삭제했습니다.");
    }

    private String getContent(InputStream file) {
        InputStreamReader inputStreamReader = new InputStreamReader(file);
        Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
        return streamOfString.collect(Collectors.joining("\n"));
    }

    @Transactional
    private CommonResponseDto save_markdown(ArticleSaveDto articleSaveDto, List<MultipartFile> imageFiles) {
        String SERVER_ADDRESS = "https://dvlprjw.p-e.kr";
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(articleSaveDto.getContent());
        String content = articleSaveDto.getContent();
        ArrayList<String> imageNames = new ArrayList<>();

        while (image_in_articleData.find()) {
            String oldImageDirectory = image_in_articleData.group();
            String[] oldImageDirectoryList = oldImageDirectory.split("/");
            String newImageName = java.lang.System.currentTimeMillis() + "_" + oldImageDirectoryList[oldImageDirectoryList.length-1];
            newImageName = newImageName.substring(0, newImageName.length() - 1); // 마지막에 붙는 닫는 괄호, ) 제거
            String newImageDirectory = oldImageDirectory.replace(oldImageDirectory.split("!\\[(.*?)]\\(")[1], SERVER_ADDRESS + "/blog-api/image/" + newImageName + ")");
            imageNames.add(newImageName);
            content = content.replace(oldImageDirectory, newImageDirectory);
        }
        articleSaveDto.setContent(content);

        Long articleId = articleRepository.save(articleSaveDto.toEntity()).getId();

        String result_save_images = save_images(imageFiles, imageNames, articleId);
        if (!result_save_images.equals("이미지 저장에 성공했습니다")) {
            return new CommonResponseDto("Save Article", "Failed", result_save_images);
        }
        return new CommonResponseDto("Save Article", "Success", "등록되었습니다");
    }

    @Transactional
    private String save_images(List<MultipartFile> imageFiles, List<String> imageNames, Long articleId) {
        String return_val = "이미지 저장에 실패했습니다";
        short result = -1;
        int imageIdx = 0;
        ImageSaveDto imageSaveDto = new ImageSaveDto();

        try {
            imageSaveDto.setRootPath();
            for (MultipartFile imageFile : imageFiles) {
                String directory = imageSaveDto.saveImage(imageFile, imageNames.get(imageIdx));
                imageRepository.save(ImageSaveDto.builder().article(articleRepository.findById(articleId).orElse(null)).directory(directory).build().toEntity());
                result++;
                imageIdx++;
            }

            System.out.println("이미지 저장 완료");
        } catch (Exception e) {
            System.out.println("에러 : " + e.getMessage());
        }

        if (result == imageFiles.size() - 1) return_val = "이미지 저장에 성공했습니다";
        return return_val;
    }

    private Integer count_image(String articleData) {
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(articleData);

        int cnt = 0;
        while (image_in_articleData.find())
            cnt++;

        return cnt;
    }

    // Todo - 파일명 맞는지 확인
    private boolean check_image(String articleData, ImageSaveDto requestDto) {
        return true;
    }

    public byte[] getImage(String imageName) throws IOException {
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
