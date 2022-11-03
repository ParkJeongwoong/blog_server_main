package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.entity.QInvertedIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InvertedIndexRepositoryImpl implements InvertedIndexRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ArticleRepository articleRepository;

    public List<ArticleSearchResultDto> searchArticle(List<String> words, Long offset) {
        QInvertedIndex invertedIndex = QInvertedIndex.invertedIndex;
        BooleanBuilder builder = new BooleanBuilder();
        words.forEach(word->builder.or(invertedIndex.term.contains(word)));

        return jpaQueryFactory.select(invertedIndex.documentId)
                                .from(invertedIndex)
                                .where(builder).limit(101).offset(offset)
                                .groupBy(invertedIndex.documentId)
                                .orderBy(invertedIndex.priorityScore.sum().desc())
                                .fetch()
                                .stream().map(documentId->new ArticleSearchResultDto(
                                        articleRepository.findById(documentId)
                                                .orElseThrow(()->new RuntimeException("역색인 테이블의 게시글 번호가 게시글 테이블에 존재하지 않습니다."))))
                                .collect(Collectors.toList());
    }

}
