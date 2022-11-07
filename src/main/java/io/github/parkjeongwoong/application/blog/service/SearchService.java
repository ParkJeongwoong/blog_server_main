package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleSearchResultDto;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.repository.QueryDSL.ArticleRepositoryCustom;
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
                .collect(Collectors.toList());
        List<ArticleSearchResultDto> searchResult = QinvertedIndexRepository.searchArticle(searchList, offset);
        searchResult.forEach(articleSearchResultDto -> articleSearchResultDto.findWord(wordArray));
        if (searchResult.size()>100) {
            searchResult.get(100).setMatchCount(-1);
        }
        searchResult.sort(Collections.reverseOrder());
        return searchResult;
    }

    @Transactional
    public void invertedIndexProcess() {
        invertedIndexRepository.deleteAll();
        articleRepository.findAll().forEach(this::makeInvertedIndex);
    }

    @Transactional
    public void makeInvertedIndex(Article article) {
        Map<String, InvertedIndex> processedData = new HashMap<>();
        final long[] position = {0};

        long documentId = article.getId();
        List<String> titleWords = new ArrayList<>(Arrays.asList(TextRefining.preprocessingContent(article.getTitle()).split(" "))).stream()
                .map(TextRefining::refineWord)
                .filter(str->str!=null&&!str.equals(""))
                .collect(Collectors.toList());
        List<String> contentWords = new ArrayList<>(Arrays.asList(TextRefining.preprocessingContent(article.getContent()).split(" "))).stream()
                .map(TextRefining::refineWord)
                .filter(str->str!=null&&!str.equals(""))
                .collect(Collectors.toList());

        // InvertedIndex 생성 - title
        titleWords.forEach(word->{
            if (word.length() > 20) return;
            if (processedData.containsKey(word)) {
                processedData.get(word).addPriorityScore("title");
            }
            else {
                processedData.put(word, InvertedIndex.builder()
                        .term(word)
                        .documentId(documentId)
                        .firstPosition(position[0])
                        .textType("title")
                        .build());
            }
            position[0] ++;
        });

        // InvertedIndex 생성 - content
        contentWords.forEach(word->{
            if (word.length() > 20) return;
            if (processedData.containsKey(word)) {
                processedData.get(word).addPriorityScore("content");
            }
            else {
                processedData.put(word, InvertedIndex.builder()
                        .term(word)
                        .documentId(documentId)
                        .firstPosition(position[0])
                        .textType("content")
                        .build());
            }
            position[0] ++;
        });

        processedData.values().forEach(invertedIndexRepository::save);
    }

}
