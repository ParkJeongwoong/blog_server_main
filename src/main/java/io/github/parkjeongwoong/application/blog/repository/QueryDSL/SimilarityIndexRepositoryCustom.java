package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.entity.SimilarityIndex;

import java.util.List;

public interface SimilarityIndexRepositoryCustom {
    List<SimilarityIndex> getSimilarityIndexByDocumentIdList(List<Long> documentIdList);
}
