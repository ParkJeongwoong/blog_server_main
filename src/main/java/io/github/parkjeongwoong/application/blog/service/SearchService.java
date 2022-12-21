package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.InvertedIndexRepositoryCustom;
import io.github.parkjeongwoong.application.blog.service.textRefine.TextRefining;
import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.InvertedIndex;
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
        invertedIndexRepository.deleteAll(); // Todo - 이거 지우기
        articleRepository.findAll().forEach(this::makeInvertedIndex);
        invertedIndexRepository.findAll().forEach(invertedIndex -> {
            invertedIndex.TFIDF(QinvertedIndexRepository.getDocumentFrequency(invertedIndex.getTerm()), articleCount);
            invertedIndexRepository.save(invertedIndex);
        });
    }

    @Transactional
    public void makeInvertedIndex(Article article) {
        Map<String, InvertedIndex> processedData = new HashMap<>();
        final long[] position = {0};
        long documentId = article.getId();

        List<String> titleWords = makeRefinedWords(article.getTitle());
        List<String> contentWords = makeRefinedWords(article.getContent());

        createProcessedInvertedIndexData(titleWords, "title", documentId, position, processedData);
        createProcessedInvertedIndexData(contentWords, "content", documentId, position, processedData);

        processedData.values().forEach(invertedIndexRepository::save);
    }

    private List<String> makeRefinedWords(String rawStringData) {
        return new ArrayList<>(Arrays.asList(TextRefining.preprocessingContent(rawStringData).split(" "))).stream()
                .map(TextRefining::refineWord)
                .filter(str->str!=null&&!str.equals(""))
                .collect(Collectors.toList());
    }

    private void createProcessedInvertedIndexData(List<String> words, String textType, long documentId, long[] position, Map<String, InvertedIndex> processedData) {
        long articleCount = articleRepository.count();

        words.forEach(word->{
            if (word.length() > 20) return;
            if (processedData.containsKey(word)) {
                processedData.get(word).addTermFrequency(textType);
            }
            else {
                processedData.put(word, InvertedIndex.builder()
                        .term(word)
                        .documentId(documentId)
                        .firstPosition(position[0])
                        .textType(textType)
                        .build());
            }
            position[0] ++;
        });
    }

    // Todo - 글 수정 삭제 이후의 작업

}
