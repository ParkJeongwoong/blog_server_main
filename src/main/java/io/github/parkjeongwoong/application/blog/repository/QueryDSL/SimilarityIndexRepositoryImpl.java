package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.parkjeongwoong.entity.QSimilarityIndex;
import io.github.parkjeongwoong.entity.SimilarityIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SimilarityIndexRepositoryImpl implements SimilarityIndexRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SimilarityIndex> getSimilarityIndexByDocumentIdList(List<Long> documentIdList) {
        QSimilarityIndex similarityIndex = QSimilarityIndex.similarityIndex;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(similarityIndex.documentId.in(documentIdList));
        builder.and(similarityIndex.counterDocumentId.in(documentIdList));
//        documentIdList.forEach(documentId->builder.or(similarityIndex.documentId.eq(documentId)));

        return jpaQueryFactory.selectFrom(similarityIndex)
                .where(builder)
                .fetch();
    }

}
