package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.application.blog.dto.ArticleResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a ORDER BY a.id DESC")
    List<Article> findAllDesc();

    @Query("SELECT A FROM Article A WHERE CATEGORY = :category and CATEGORY_ID = :categoryId")
    ArticleResponseDto findByCategoryAndId(@Param("category") String category, @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(1) FROM Article A WHERE CATEGORY = :category")
    long countCategory(@Param("category") String category);

    @Modifying
    @Query("UPDATE Article a SET a.content = :content WHERE a.id = :articleId")
    void updateById(@Param("articleId") Long articleId, @Param("content") String content);

}