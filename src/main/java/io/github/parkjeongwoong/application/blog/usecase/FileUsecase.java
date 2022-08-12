package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.ArticleUpdateRequestDto;
import io.github.parkjeongwoong.application.blog.dto.CommonResponseDto;
import io.github.parkjeongwoong.application.blog.dto.ImageSaveRequestDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleSaveRequestDto;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

public interface FileUsecase {
    CommonResponseDto saveArticle(MultipartHttpServletRequest multiRequest, ArticleSaveRequestDto requestDto, ImageSaveRequestDto imageSaveRequestDto);
    CommonResponseDto updateArticle(Long articleId, ArticleUpdateRequestDto requestDto);
    CommonResponseDto deleteArticle(Long articleId);
    byte[] getImage(String imageName) throws IOException;
}
