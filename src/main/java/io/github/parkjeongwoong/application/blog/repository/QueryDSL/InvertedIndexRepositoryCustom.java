package io.github.parkjeongwoong.application.blog.repository.QueryDSL;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;

import java.util.List;
import java.util.Map;

public interface InvertedIndexRepositoryCustom {
    List<ArticleSearchResultDto> searchArticle(List<String> words, Long offset);
    long getDocumentFrequency(String term);
    Map<String, Long> getDocumentFrequencyByTerm();
}
