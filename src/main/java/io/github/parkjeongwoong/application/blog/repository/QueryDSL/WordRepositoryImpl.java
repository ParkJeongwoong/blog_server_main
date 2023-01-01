package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.parkjeongwoong.application.blog.dto.Word_SimilarityScoreDto;
import io.github.parkjeongwoong.entity.QArticle;
import io.github.parkjeongwoong.entity.QWord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Word_SimilarityScoreDto> findAllByIsUpdatedTrue() {
        QWord word = QWord.word;
        QArticle article = QArticle.article;

        long articleCount = jpaQueryFactory.select(article.count())
                .from(article).fetchFirst();

        return jpaQueryFactory.select(Projections.constructor(Word_SimilarityScoreDto.class, word, Expressions.constant(articleCount)))
                .from(word)
                .where(word.isUpdated.eq(true))
                .fetch();
    }

    @Override
    public List<Word_SimilarityScoreDto> findAllByIdWithArticleCount(List<String> termList) {
        QWord word = QWord.word;
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(word.term.in(termList));

        long articleCount = jpaQueryFactory.select(article.count())
                .from(article).fetchFirst();

        return jpaQueryFactory.select(Projections.constructor(Word_SimilarityScoreDto.class, word, Expressions.constant(articleCount)))
                .from(word)
                .where(builder)
                .fetch();
    }

    @Override
    public List<Word_SimilarityScoreDto> findAllWithArticleCount() {
        QWord word = QWord.word;
        QArticle article = QArticle.article;

        long articleCount = jpaQueryFactory.select(article.count())
                .from(article).fetchFirst();

        return jpaQueryFactory.select(Projections.constructor(Word_SimilarityScoreDto.class, word, Expressions.constant(articleCount)))
                .from(word)
                .fetch();
    }

}
