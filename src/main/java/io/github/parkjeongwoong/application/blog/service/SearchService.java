package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.application.blog.dto.Word_SimilarityScoreDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.InvertedIndexRepositoryCustom;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.WordRepositoryCustom;
import io.github.parkjeongwoong.application.blog.repository.WordRepository;
import io.github.parkjeongwoong.application.blog.service.textRefine.TextRefining;
import io.github.parkjeongwoong.application.blog.usecase.RecommendationUsecase;
import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.InvertedIndex;
import io.github.parkjeongwoong.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService implements SearchUsecase {

    private final InvertedIndexRepository invertedIndexRepository;
    private final InvertedIndexRepositoryCustom QinvertedIndexRepository;
    private final ArticleRepository articleRepository;
    private final WordRepository wordRepository;
    private final WordRepositoryCustom QwordRepository;
    private final RecommendationUsecase recommendationUsecase;

    public List<ArticleSearchResultDto> searchArticle(String words, long offset) {
        String[] wordArray = words.split(" ");
        List<String> searchList = new ArrayList<>(Arrays.asList(wordArray)).stream()
                .filter(str->str!=null&&!str.equals(""))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<ArticleSearchResultDto> searchResult = QinvertedIndexRepository.searchArticle(searchList, offset);
        searchResult.forEach(ArticleSearchResultDto::findWord);
        if (searchResult.size()>100) {
            searchResult.get(100).setPriorityScore(-1);
        }
        searchResult.sort(Collections.reverseOrder()); // 삭제 예정
        return searchResult;
    }

    @Transactional
    public void invertedIndexProcess() {
        long articleCount = articleRepository.count();
        Map<String, Long> termToDF = QinvertedIndexRepository.getDocumentFrequencyByTerm();

        articleRepository.findAll().forEach(this::createInvertedIndex); // Inverted_index, Word 재생성
        invertedIndexRepository.findAll().forEach(invertedIndex -> {
            invertedIndex.TFIDF(termToDF.get(invertedIndex.getTerm()), articleCount);
            invertedIndexRepository.save(invertedIndex);
        });
    }

    @Transactional
    public void createInvertedIndex(Article article) {
        Map<String, InvertedIndex> processedData = new HashMap<>();
        final long[] position = {0};
        long documentId = article.getId();
        deleteWordByDocumentId_wordEffectOnly(documentId);
        invertedIndexRepository.deleteAllByDocumentId(documentId);

        List<String> titleWords = makeRefinedWords(article.getTitle());
        List<String> contentWords = makeRefinedWords(article.getContent());
        saveDocumentWords(titleWords, contentWords);

        createProcessedInvertedIndexData(titleWords, "title", documentId, position, processedData);
        createProcessedInvertedIndexData(contentWords, "content", documentId, position, processedData);

        processedData.values().forEach(invertedIndexRepository::save);
    }

    @Transactional
    private void saveWord(String term) {
        Word word = wordRepository.findById(term).orElse(null);
        if (word == null) {
            word = Word.builder().term(term).build();
        }
        word.addDocumentFrequency();
        wordRepository.save(word);
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

    @Transactional
    private void deleteWordByDocumentId_similarityUpdate(long documentId) {
        List<String> termList = invertedIndexRepository.findAllByDocumentId(documentId).stream().map(InvertedIndex::getTerm).collect(Collectors.toList());
        termList.forEach(this::deleteWord);
        List<Word_SimilarityScoreDto> wordSimilarityScoreDtoList = QwordRepository.findAllByIdWithArticleCount(termList);
        wordSimilarityScoreDtoList.forEach(recommendationUsecase::updateSimilarityByWord);
    }

    @Transactional
    private void deleteWordByDocumentId_wordEffectOnly(long documentId) {
        List<String> termList = invertedIndexRepository.findAllByDocumentId(documentId).stream().map(InvertedIndex::getTerm).collect(Collectors.toList());
        termList.forEach(this::deleteWord);
    }

    private void createProcessedInvertedIndexData(List<String> words, String textType, long documentId, long[] position, Map<String, InvertedIndex> processedData) {
        words.forEach(word->{
            if (word.length() > 20) return;
            if (processedData.containsKey(word)) {
                processedData.get(word).addTermFrequency(textType);
            }
            else {
                processedData.put(word, InvertedIndex.builder()
                        .term(word).documentId(documentId).firstPosition(position[0]).textType(textType).build());
            }
            position[0] ++;
        });
    }

    private void saveDocumentWords(List<String> titleWords, List<String> contentWords) {
        List<String> totalWords = new ArrayList<>();
        totalWords.addAll(titleWords);
        totalWords.addAll(contentWords);
        Set<String> wordSets = new HashSet<>(totalWords);
        wordSets.forEach(this::saveWord);
    }

    private List<String> makeRefinedWords(String rawStringData) {
        return new ArrayList<>(Arrays.asList(TextRefining.preprocessingContent(rawStringData).split(" "))).stream()
                .map(TextRefining::refineWord)
                .filter(str-> !str.equals(""))
                .collect(Collectors.toList());
    }

    // Todo - 글 수정 삭제 이후의 작업

}
