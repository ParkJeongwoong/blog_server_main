package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.DocumentFrequencyDto;
import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;
import io.github.parkjeongwoong.application.blog.dto.Word_SimilarityScoreDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.SimilarityIndexRepositoryCustom;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.WordRepositoryCustom;
import io.github.parkjeongwoong.application.blog.repository.SimilarityRepository;
import io.github.parkjeongwoong.application.blog.repository.WordRepository;
import io.github.parkjeongwoong.application.blog.usecase.RecommendationUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.InvertedIndex;
import io.github.parkjeongwoong.entity.SimilarityIndex;
import io.github.parkjeongwoong.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService implements RecommendationUsecase {

    private final ArticleRepository articleRepository;
    private final InvertedIndexRepository invertedIndexRepository;
    private final SimilarityRepository similarityRepository;
    private final SimilarityIndexRepositoryCustom QsimilarityIndexRepository;
    private final WordRepository wordRepository;
    private final WordRepositoryCustom QwordRepository;

    public List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId) {
        return similarityRepository.findTop5ByDocumentIdOrderBySimilarityScoreDesc(documentId)
                .stream().map(entity-> new RecommendedArticleResponseDto(
                        articleRepository.findById(entity.getCounterDocumentId())
                                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = " + entity.getDocumentId()))))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createSimilarityIndex(long documentId) {
        List<SimilarityIndex> similarityIndexList = new ArrayList<>();
        List<Article> articleList = articleRepository.findAll();
        articleList.forEach(article -> {
            if (article.getId() == documentId) return;
            similarityIndexList.add(SimilarityIndex.builder()
                    .documentId(documentId)
                    .counterDocumentId(article.getId())
                    .build());
            similarityIndexList.add(SimilarityIndex.builder()
                    .documentId(article.getId())
                    .counterDocumentId(documentId)
                    .build());
        });
        similarityRepository.saveAll(similarityIndexList);
    }

    @Transactional
    public void resetAllSimilarity() {
        similarityRepository.deleteAll();

        List<Article> articleList = articleRepository.findAll();
        articleList.forEach(article -> createSimilarityIndex(article.getId()));

        List<Word_SimilarityScoreDto> wordSimilarityScoreDtoList = QwordRepository.findAllWithArticleCount();
        wordSimilarityScoreDtoList.forEach(this::addSimilarityScoreByWord);
    }

    @Transactional
    public void updateSimilarity() {
        // Similarity 업데이트
        List<Word_SimilarityScoreDto> wordSimilarityScoreDtoList = QwordRepository.findAllByIsUpdatedTrue();
        wordSimilarityScoreDtoList.forEach(this::updateSimilarityByWord);
    }

    @Transactional
    public void updateAllSimilarity() {
        // Similarity 전체 업데이트
        List<Word_SimilarityScoreDto> wordSimilarityScoreDtoList = QwordRepository.findAllWithArticleCount();
        wordSimilarityScoreDtoList.forEach(this::updateSimilarityByWord);
    }

    @Transactional
    public void updateSimilarityByWord(Word_SimilarityScoreDto wordSimilarityScoreDto) {
        Word word = wordSimilarityScoreDto.getWord();
        // 단어별 Similarity 업데이트
        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByTerm(word.getTerm());
        List<Long> documentIdList = invertedIndexList.stream().map(InvertedIndex::getDocumentId).collect(Collectors.toList());
        List<SimilarityIndex> similarityIndexList = QsimilarityIndexRepository.getSimilarityIndexByDocumentIdList(documentIdList);

        Map<Long, InvertedIndex> invertedIndexMap = getInvertedIndexMap(invertedIndexList);
        Map<Long, List<SimilarityIndex>> similarityIndexMap = getSimilarityIndexMap(similarityIndexList, documentIdList);

        subSimilarityScore(invertedIndexList, invertedIndexMap, similarityIndexMap);
        addSimilarityScore(wordSimilarityScoreDto, invertedIndexList, invertedIndexMap, similarityIndexMap);

        word.updateFinished();

        invertedIndexRepository.saveAll(invertedIndexList);
        similarityRepository.saveAll(similarityIndexList);
        wordRepository.save(word);
    }

    private void addSimilarityScoreByWord(Word_SimilarityScoreDto wordSimilarityScoreDto) {
        Word word = wordSimilarityScoreDto.getWord();

        // Get List
        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByTerm(word.getTerm());
        List<Long> documentIdList = invertedIndexList.stream().map(InvertedIndex::getDocumentId).collect(Collectors.toList());
        List<SimilarityIndex> similarityIndexList = QsimilarityIndexRepository.getSimilarityIndexByDocumentIdList(documentIdList);

        // Get Map
        Map<Long, InvertedIndex> invertedIndexMap = getInvertedIndexMap(invertedIndexList);
        Map<Long, List<SimilarityIndex>> similarityIndexMap = getSimilarityIndexMap(similarityIndexList, documentIdList);

        // Create Similarity Index By Word
        addSimilarityScore(wordSimilarityScoreDto, invertedIndexList, invertedIndexMap, similarityIndexMap);

        similarityRepository.saveAll(similarityIndexList);
    }

    private void addSimilarityScore(Word_SimilarityScoreDto wordSimilarityScoreDto,
                                    List<InvertedIndex> invertedIndexList,
                                    Map<Long, InvertedIndex> invertedIndexMap,
                                    Map<Long, List<SimilarityIndex>> similarityIndexMap) {
        for (InvertedIndex invertedIndex:invertedIndexList) {
            for (SimilarityIndex similarityIndex : similarityIndexMap.get(invertedIndex.getDocumentId())) {
                InvertedIndex counterInvertedIndex = invertedIndexMap.get(similarityIndex.getCounterDocumentId());
                DocumentFrequencyDto documentFrequencyDto = DocumentFrequencyDto.builder().wordSimilarityScoreDto(wordSimilarityScoreDto).build();
                double newScore = invertedIndex.TFIDF(documentFrequencyDto) * counterInvertedIndex.TFIDF(documentFrequencyDto);
                similarityIndex.addScore(newScore);
            }
        }
    }

    private void subSimilarityScore(List<InvertedIndex> invertedIndexList,
                                    Map<Long, InvertedIndex> invertedIndexMap,
                                    Map<Long, List<SimilarityIndex>> similarityIndexMap) {
        for (InvertedIndex invertedIndex:invertedIndexList) {
            for (SimilarityIndex similarityIndex : similarityIndexMap.get(invertedIndex.getDocumentId())) {
                InvertedIndex counterInvertedIndex = invertedIndexMap.get(similarityIndex.getCounterDocumentId());
                double oldScore = invertedIndex.getPriorityScore() * counterInvertedIndex.getPriorityScore();
                similarityIndex.subScore(oldScore);
            }
        }
    }

    private Map<Long, InvertedIndex> getInvertedIndexMap(List<InvertedIndex> invertedIndexList) {
        return invertedIndexList.stream().collect(Collectors.toMap(InvertedIndex::getDocumentId,invertedIndex -> invertedIndex));
    }

    private Map<Long, List<SimilarityIndex>> getSimilarityIndexMap(List<SimilarityIndex> similarityIndexList, List<Long> documentIdList) {
        Map<Long, List<SimilarityIndex>> similarityIndexMap = new HashMap<>();
        for (long documentID:documentIdList) {
            List<SimilarityIndex> sub_similarityIndexList = similarityIndexList.stream()

                    .filter(similarityIndex -> similarityIndex.getDocumentId()==documentID)
                    .collect(Collectors.toList());
            similarityIndexMap.put(documentID, sub_similarityIndexList);
        }
        return similarityIndexMap;
    }
}
