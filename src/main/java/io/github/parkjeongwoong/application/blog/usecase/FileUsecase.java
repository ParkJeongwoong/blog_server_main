package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.ArticleUpdateRequestDto;
import io.github.parkjeongwoong.application.blog.dto.CommonResponseDto;
import io.github.parkjeongwoong.application.blog.dto.ImageSaveDto;
import io.github.parkjeongwoong.application.blog.dto.ArticleSaveDto;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

public interface FileUsecase {
    CommonResponseDto saveArticle(MultipartHttpServletRequest multiRequest);
    CommonResponseDto updateArticle(Long articleId, ArticleUpdateRequestDto requestDto);
    CommonResponseDto updateArticle_markdown(Long articleId, MultipartHttpServletRequest multiRequest, ArticleSaveDto requestDto, ImageSaveDto imageSaveRequestDto);
    CommonResponseDto deleteArticle(Long articleId);
    byte[] getImage(String imageName) throws IOException;
}
