package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleResponseDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleUpdateRequestDto;
import io.github.parkjeongwoong.application.blog.dto.CommonResponseDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.usecase.FileUsecase;
import io.github.parkjeongwoong.application.blog.usecase.RecommendationUsecase;
import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.application.blog.repository.ImageRepository;
import io.github.parkjeongwoong.entity.Image;
import io.github.parkjeongwoong.etc.ServerState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class FileService implements FileUsecase {
    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final SearchUsecase searchUsecase;
    private final RecommendationUsecase recommendationUsecase;

    @Autowired
    private final RedisTemplate redisTemplate;
    @Autowired
    private ServerState serverState;

    @Transactional
    public CommonResponseDto saveArticle(MultipartHttpServletRequest multiRequest) {
        try {
            MultipartFile multipartFile = multiRequest.getFile("markdown");
            List<MultipartFile> imageFiles = multiRequest.getFiles("images");

            String category = multiRequest.getParameter("category");
            String subCategory = multiRequest.getParameter("subCategory");
            long categoryId = articleRepository.countCategory(category);

            Article article = new Article(multipartFile, category, subCategory, categoryId);

            if (compareImageCnt(imageFiles.size(), article.countImage())) throw new InputMismatchException("첨부한 이미지 개수가 파일의 이미지 개수와 일치하지 않습니다");
            ArrayList<String> changedImageNames = article.changeImageDirectory();

            long articleId = articleRepository.save(article).getId();
            save_images(imageFiles, changedImageNames, articleId);
            searchUsecase.createInvertedIndex(article); // 검색 역색인 생성
            recommendationUsecase.createSimilarityIndex(articleId); // 추천 유사도 생성
            recommendationUsecase.updateAllSimilarity(); // 추천 유사도 적용
            serverState.articleIsUpdated();
            return new CommonResponseDto("Save Article", "Success", "등록되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new CommonResponseDto("Save Article", "Failed", e.getMessage());
        }
    }

    @Transactional
    public CommonResponseDto updateArticle_string(Long articleId, ArticleUpdateRequestDto requestDto) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = "+ articleId));
        article.update(null, requestDto.getContent());
        // Todo - 역색인, 추천
        // Redis 업데이트
        ValueOperations<String, ArticleResponseDto> valueOperations = redisTemplate.opsForValue();
        String redis_key = "a"+article.getCategory()+article.getCategoryId();
        ArticleResponseDto article_redis = valueOperations.get(redis_key);
        serverState.articleIsUpdated();
        if (article_redis != null) {
            article_redis.setContent(requestDto.getContent());
            valueOperations.set(redis_key, article_redis, 7, TimeUnit.DAYS);
        }
        return new CommonResponseDto("Update Article", "Success", "게시글을 성공적으로 수정했습니다.");
    }

    @Transactional
    public CommonResponseDto updateArticle_markdown(Long articleId, MultipartHttpServletRequest multiRequest) {
        return null;
    }

    @Transactional
    public CommonResponseDto deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = "+ articleId));
        articleRepository.delete(article);
        serverState.articleIsUpdated();
        return new CommonResponseDto("Delete Article", "Success", "게시글을 성공적으로 삭제했습니다.");
    }

    private boolean compareImageCnt(int uploadedImageCnt, int articleImageCnt) throws InputMismatchException {
        System.out.println("업로드된 이미지 개수 : " + uploadedImageCnt);
        System.out.println("파일의 이미지 개수 : " + articleImageCnt);
        return uploadedImageCnt != articleImageCnt;
    }

    private void save_images(List<MultipartFile> imageFiles, List<String> imageNames, Long articleId) {
        Image image = new Image();
        int imageIdx = 0;

        try {
            for (MultipartFile imageFile : imageFiles) {
                imageRepository.save(
                        Image.builder()
                        .article(articleRepository.findById(articleId).orElse(null))
                        .directory(image.saveImageFile(imageFile, imageNames.get(imageIdx)))
                        .build());
                imageIdx++;
            }

            System.out.println("이미지 저장 완료");
        } catch (Exception e) {
            System.out.println("에러 : " + e.getMessage());
        }

        if (imageIdx != imageFiles.size()) throw new RuntimeException("이미지 저장 중 문제가 발생했습니다");
    }
}
