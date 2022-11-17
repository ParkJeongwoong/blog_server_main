package io.github.parkjeongwoong.application.blog.service;

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

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService implements RecommendationUsecase {

    private final ArticleRepository articleRepository;
    private final InvertedIndexRepository invertedIndexRepository;
    private final SimilarityRepository similarityRepository;

    public List<SimilarityIndex> get5SimilarArticle(long documentId) {
        return similarityRepository.findTop5ByDocumentIdOrderBySimilarityScoreDesc(documentId);
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

    @Transactional
    public void saveSimilarArticle(long documentId) {
        System.out.println("Log. Document # " + documentId);
        // Todo : 성능 개선
        // invertedIndex에서 동일한 term을 가진 두 문서의 priorityScore를 곱해서 유사도 점수를 파악
        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByDocumentId(documentId);
        invertedIndexList.forEach(invertedIndex -> {
            String term = invertedIndex.getTerm();
            long termScore = invertedIndex.getPriorityScore();
            
            invertedIndexRepository.findAllByTerm(term).forEach(index -> {
                if (documentId == index.getDocumentId()) return;
                SimilarityIndex similarityIndex = similarityRepository.findById(new SimilarityIndexKey(documentId, index.getDocumentId()))
                        .orElse(null);
                if (similarityIndex == null) similarityIndex = SimilarityIndex.builder()
                                                                .documentId(documentId)
                                                                .counterDocumentId(index.getDocumentId())
                                                                .build();
                long multipledScore = termScore * index.getPriorityScore();
                similarityIndex.addScore(multipledScore);
                similarityRepository.save(similarityIndex);
            });
        });
    }
}
