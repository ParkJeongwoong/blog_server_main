package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.RecommendedArticleResponseDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.SimilarityIndexRepositoryImpl;
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
    private final SimilarityIndexRepositoryImpl QsimilarityIndexRepository;
    private final WordRepository wordRepository;

    public List<RecommendedArticleResponseDto> get5SimilarArticle(long documentId) {
        return similarityRepository.findTop5ByDocumentIdOrderBySimilarityScoreDesc(documentId)
                .stream().map(entity-> new RecommendedArticleResponseDto(
                        articleRepository.findById(entity.getCounterDocumentId())
                                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. articleId = " + entity.getDocumentId()))))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveWord(String term) {
        Word word = wordRepository.findById(term).orElse(null);
        if (word == null) {
            word = Word.builder().term(term).build();
        }
        word.addDocumentFrequency();
        wordRepository.save(word);
    }

    @Transactional
    public void deleteWordByDocumentId_similarityUpdate(long documentId) {
        List<String> termList = invertedIndexRepository.findAllByDocumentId(documentId).stream().map(InvertedIndex::getTerm).collect(Collectors.toList());
        termList.forEach(this::deleteWord);
        long articleCount = articleRepository.count(); // TF-IDF 의 DF 때문에 항상 article 작업이 끝난 이후 본 메서드 사용
        List<Word> wordList = wordRepository.findAllById(termList);
        wordList.forEach(word -> updateSimilarityByWord(word,articleCount));
    }

    @Transactional
    public void deleteWordByDocumentId_wordEffectOnly(long documentId) {
        List<String> termList = invertedIndexRepository.findAllByDocumentId(documentId).stream().map(InvertedIndex::getTerm).collect(Collectors.toList());
        termList.forEach(this::deleteWord);
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

        List<Word> wordList = wordRepository.findAll();
        long articleCount = articleList.size();

        for (Word word:wordList) {
            addSimilarityScoreByWord(word, articleCount);
        }
    }

    @Transactional
    public void updateSimilarity() {
        // Similarity 업데이트
        List<Word> updatedWords = wordRepository.findAllByIsUpdatedTrue();
        long articleCount = articleRepository.count();
        updatedWords.forEach(word -> updateSimilarityByWord(word, articleCount));
    }

    @Transactional
    public void updateAllSimilarity() {
        // Similarity 전체 업데이트
        List<Word> updatedWords = wordRepository.findAll();
        long articleCount = articleRepository.count();
        updatedWords.forEach(word -> updateSimilarityByWord(word, articleCount));
    }

    @Transactional
    private void deleteWord(String term) {
        Word word = wordRepository.findById(term).orElse(null);
        if (word == null) {
            return;
        }
        word.subDocumentFrequency();
        wordRepository.save(word);
    }

    private void addSimilarityScoreByWord(Word word, long articleCount) {
        // Get List
        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByTerm(word.getTerm());
        List<Long> documentIdList = invertedIndexList.stream().map(InvertedIndex::getDocumentId).collect(Collectors.toList());
        List<SimilarityIndex> similarityIndexList = QsimilarityIndexRepository.getSimilarityIndexByDocumentIdList(documentIdList);

        // Get Map
        Map<Long, InvertedIndex> invertedIndexMap = getInvertedIndexMap(invertedIndexList);
        Map<Long, List<SimilarityIndex>> similarityIndexMap = getSimilarityIndexMap(similarityIndexList, documentIdList);

        // Create Similarity Index By Word
        addSimilarityScore(word, articleCount, invertedIndexList, invertedIndexMap, similarityIndexMap);

        similarityRepository.saveAll(similarityIndexList);
    }

    @Transactional
    private void updateSimilarityByWord(Word word, long articleCount) {
        // 단어별 Similarity 업데이트
        List<InvertedIndex> invertedIndexList = invertedIndexRepository.findAllByTerm(word.getTerm());
        List<Long> documentIdList = invertedIndexList.stream().map(InvertedIndex::getDocumentId).collect(Collectors.toList());
        List<SimilarityIndex> similarityIndexList = QsimilarityIndexRepository.getSimilarityIndexByDocumentIdList(documentIdList);

        Map<Long, InvertedIndex> invertedIndexMap = getInvertedIndexMap(invertedIndexList);
        Map<Long, List<SimilarityIndex>> similarityIndexMap = getSimilarityIndexMap(similarityIndexList, documentIdList);

        subSimilarityScore(invertedIndexList, invertedIndexMap, similarityIndexMap);
        addSimilarityScore(word, articleCount, invertedIndexList, invertedIndexMap, similarityIndexMap);

        word.updateFinished();

        invertedIndexRepository.saveAll(invertedIndexList);
        similarityRepository.saveAll(similarityIndexList);
        wordRepository.save(word);
    }

    private void addSimilarityScore(Word word, long articleCount,
                                    List<InvertedIndex> invertedIndexList,
                                    Map<Long, InvertedIndex> invertedIndexMap,
                                    Map<Long, List<SimilarityIndex>> similarityIndexMap) {
        for (InvertedIndex invertedIndex:invertedIndexList) {
            for (SimilarityIndex similarityIndex : similarityIndexMap.get(invertedIndex.getDocumentId())) {
                InvertedIndex counterInvertedIndex = invertedIndexMap.get(similarityIndex.getCounterDocumentId());
                double newScore = invertedIndex.TFIDF(word.getDocumentFrequency(), articleCount)
                        * counterInvertedIndex.TFIDF(word.getDocumentFrequency(), articleCount);
                System.out.println("add " + word.getTerm() + " " + similarityIndex.getDocumentId() + " " + similarityIndex.getCounterDocumentId() + " " + newScore);
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
                System.out.println("sub " + invertedIndex.getTerm() + " " + similarityIndex.getDocumentId() + " " + similarityIndex.getCounterDocumentId() + " " + oldScore);
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
