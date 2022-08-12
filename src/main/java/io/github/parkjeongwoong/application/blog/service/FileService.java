package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleUpdateRequestDto;
import io.github.parkjeongwoong.application.blog.dto.CommonResponseDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.usecase.FileUsecase;
import io.github.parkjeongwoong.entity.Image;
import io.github.parkjeongwoong.application.blog.repository.ImageRepository;
import io.github.parkjeongwoong.application.blog.dto.ImageSaveRequestDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleSaveRequestDto;
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
    public CommonResponseDto saveArticle(MultipartHttpServletRequest multiRequest, ArticleSaveRequestDto requestDto, ImageSaveRequestDto imageSaveRequestDto) {
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
                        return new CommonResponseDto("Save Article", "Failed", "파일 이름의 첫 8자리는 작성일로 만들어주세요 (ex. 20220731_파일명)");
                    requestDto.setDate(fileName.substring(0, 8));
                }
                requestDto.setFileName(fileName);
                if (category == null || category.length() == 0)
                    return new CommonResponseDto("Save Article", "Failed", "카테고리를 입력해주세요");

                requestDto.setCategory(category);
                requestDto.setSubCategory(subCategory);

                System.out.println("fileName : " + fileName);
                System.out.println("title : " + title);

                System.out.println("업로드된 이미지 개수 : " + multipartFile_images.size());
                System.out.println("파일의 이미지 개수 : " + count_image(streamToString));
                if (multipartFile_images.size() != count_image(streamToString)) {
                    return new CommonResponseDto("Save Article", "Failed", "첨부한 이미지 개수가 파일의 이미지 개수와 일치하지 않습니다");
                }

                // 이 부분은 save_images로 (이거 때매 return 값을 boolean에서 string으로 변경)
//                if (!fileService.check_image(streamToString, imageSaveRequestDto)) {
//                    return "문서의 이미지와 업로드한 이미지가 다릅니다";
//                }

                imageNames = save_markdown(requestDto);

                String result_save_images = save_images(imageSaveRequestDto, multipartFile_images, imageNames);
                if (!result_save_images.equals("이미지 저장에 성공했습니다")) {
                    return new CommonResponseDto("Save Article", "Failed", result_save_images);
                }
            } else {
                return new CommonResponseDto("Save Article", "Failed", "파일을 첨부해주세요");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResponseDto("Save Article", "Success", "등록되었습니다");
    }

    @Transactional
    public CommonResponseDto updateArticle(Long articleId, ArticleUpdateRequestDto requestDto) {
        try {
            String content = requestDto.getContent();
            articleRepository.updateById(articleId, content);
            return new CommonResponseDto("Update Article", "Success", "게시글을 성공적으로 수정했습니다.");
        } catch (Exception e) {
            System.out.println(e);
            return new CommonResponseDto("Update Article", "Failed", "게시글 수정 중 문제가 발생했습니다.");
        }
    }

    public CommonResponseDto deleteArticle(Long articleId) {
        try {
            articleRepository.deleteById(articleId);
            return new CommonResponseDto("Delete Article", "Success", "게시글을 성공적으로 삭제했습니다.");
        } catch(Exception e) {
            System.out.println(e);
            return new CommonResponseDto("Delete Article", "Failed", "게시글 삭제 중 문제가 발생했습니다.");
        }
    }

    @Transactional
    private ArrayList<String> save_markdown(ArticleSaveRequestDto requestDto) {
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

        long categoryId = articleRepository.countCategory(requestDto.getCategory()) + 1;
        requestDto.setCategoryId(categoryId);
        imageNames.add(articleRepository.save(requestDto.toEntity()).getId().toString());
        return imageNames;
    }

    @Transactional
    private String save_images(ImageSaveRequestDto requestDto, List<MultipartFile> images, ArrayList<String> imageNames) {
        String return_val = "이미지 저장에 실패했습니다";
        short result = -1;
        int imageIdx = 0;
        Long imageArticleId = Long.valueOf(imageNames.remove(imageNames.size()-1));

        requestDto.setArticle(articleRepository.findById(imageArticleId).orElse(null));
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
            List<Image>ListImages = new ArrayList<>();

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

        if (result == images.size() - 1) return_val = "이미지 저장에 성공했습니다";
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
    private boolean check_image(String articleData, ImageSaveRequestDto requestDto) {
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
