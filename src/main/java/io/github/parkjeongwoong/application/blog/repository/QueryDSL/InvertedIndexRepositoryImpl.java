package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.entity.QInvertedIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static com.querydsl.core.group.GroupBy.*;

@Repository
@RequiredArgsConstructor
public class InvertedIndexRepositoryImpl implements InvertedIndexRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ArticleRepository articleRepository;

    public List<ArticleSearchResultDto> searchArticle(List<String> words, Long offset) {
        QInvertedIndex invertedIndex = QInvertedIndex.invertedIndex;
        BooleanBuilder builder = new BooleanBuilder();
        words.forEach(word->builder.or(invertedIndex.term.contains(word)));

        return jpaQueryFactory.select(invertedIndex.documentId, invertedIndex.priorityScore.sum())
                .from(invertedIndex)
                .where(builder).limit(101).offset(offset)
                .groupBy(invertedIndex.documentId)
                .fetch()
                .stream().map(result->new ArticleSearchResultDto(
                        articleRepository.findById(result.get(invertedIndex.documentId))
                                .orElseThrow(()->new RuntimeException("역색인 테이블의 게시글 번호가 게시글 테이블에 존재하지 않습니다."))
                        , result.get(invertedIndex.priorityScore.sum())
                        , words))
                .collect(Collectors.toList());
    }

    public long getDocumentFrequency(String term) {
        QInvertedIndex invertedIndex = QInvertedIndex.invertedIndex;
        return jpaQueryFactory.selectFrom(invertedIndex)
                .where(invertedIndex.term.eq(term))
                .fetch().size();
    }

    public Map<String, Long> getDocumentFrequencyByTerm() {
        QInvertedIndex invertedIndex = QInvertedIndex.invertedIndex;
        return jpaQueryFactory.selectFrom(invertedIndex)
                .groupBy(invertedIndex.term)
                .transform(groupBy(invertedIndex.term).as(invertedIndex.count()));
    }

}
