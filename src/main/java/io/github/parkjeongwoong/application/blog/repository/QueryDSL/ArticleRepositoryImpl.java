package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.entity.QArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ArticleSearchResultDto> searchByWords(List<String> words, Long offset) {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();
        words.forEach(word->builder.or(article.content.like(word)).or(article.title.like(word)));

        return jpaQueryFactory.selectFrom(article)
                .where(builder).limit(11).offset(offset)
                .fetch()
                .stream().map(ArticleSearchResultDto::new)
                .collect(Collectors.toList());
    }
}
