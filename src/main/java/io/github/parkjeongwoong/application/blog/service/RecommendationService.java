package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.SimilarityRepository;
import io.github.parkjeongwoong.application.blog.usecase.RecommendationUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.CompositeKey.SimilarityIndexKey;
import io.github.parkjeongwoong.entity.InvertedIndex;
import io.github.parkjeongwoong.entity.SimilarityIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService implements RecommendationUsecase {

    private final ArticleRepository articleRepository;
    private final InvertedIndexRepository invertedIndexRepository;
    private final SimilarityRepository similarityRepository;

    public List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId) {
        return similarityRepository.findTop5ByDocumentIdOrderBySimilarityScoreDesc(documentId)
                .stream().map(entity-> new RecommendedArticleResponseDto(
                        articleRepository.findById(entity.getCounterDocumentId())
                                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = " + entity.getDocumentId()))))
                .collect(Collectors.toList());
    }

    @Transactional
    public void makeSimilarityIndex(long offset) {
        List<Article> articleList = articleRepository.findAllDesc();
        System.out.println("ARTICLE SIZE : " + articleList.size());
        articleList.forEach(article -> {
            if (article.getId() < offset) return;
            System.out.println(article.getId() + " 유사도 분석 시작");
            saveSimilarArticle(article.getId());
            System.out.println(article.getId() + " 유사도 분석 완료//");
        });
    }

    @Transactional
    public void makeSimilarityIndex(long offset, long endpoint) {
        List<Article> articleList = articleRepository.findAllDesc();
        System.out.println("ARTICLE SIZE : " + articleList.size());
        articleList.forEach(article -> {
            if (article.getId() < offset) return;
            if (article.getId() > endpoint) return;
            System.out.println(article.getId() + " 유사도 분석 시작");
            saveSimilarArticle(article.getId());
            System.out.println(article.getId() + " 유사도 분석 완료");
        });
    }

    // Todo : 성능 개선
    @Transactional
    public void saveSimilarArticle(long documentId) {
        // invertedIndex에서 동일한 term을 가진 두 문서의 priorityScore를 곱해서 유사도 점수를 파악
        System.out.println("Log. Document # " + documentId);
        long articleCount = articleRepository.count();
        similarityRepository.deleteAllByDocumentId(documentId);

        List<SimilarityIndex> similarityIndexList = new ArrayList<>();
        List<SimilarityIndex> similarityIndexList_counterDocument = new ArrayList<>();
        for (int i=0;i<articleCount;i++) {
            similarityIndexList.add(SimilarityIndex.builder()
                    .documentId(documentId)
                    .counterDocumentId(i+1)
                    .build());
            similarityIndexList_counterDocument.add(SimilarityIndex.builder()
                    .documentId(i+1)
                    .counterDocumentId(documentId)
                    .build());
        }

        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByDocumentId(documentId);
        invertedIndexList.forEach(invertedIndex -> {
            String term = invertedIndex.getTerm();
            double termScore = invertedIndex.getPriorityScore();

            invertedIndexRepository.findAllByTerm(term).forEach(index -> {
                if (documentId == index.getDocumentId()) return;
                SimilarityIndex similarityIndex = similarityIndexList.get((int) index.getDocumentId()-1);
                SimilarityIndex similarityIndex_counterDocument = similarityIndexList_counterDocument.get((int) index.getDocumentId()-1);
                double multipliedScore = termScore * index.getPriorityScore();
                similarityIndex.addScore(multipliedScore);
                similarityIndex_counterDocument.addScore(multipliedScore);
                similarityRepository.save(similarityIndex);
                similarityRepository.save(similarityIndex_counterDocument);
            });
        });
    }
}
