package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.repository.InvertedIndexRepository;
import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import io.github.parkjeongwoong.entity.InvertedIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService implements SearchUsecase {

    private final InvertedIndexRepository invertedIndexRepository;

    public void invertedIndexProcessing(String text) {
        Map<String, InvertedIndex> processedData = new HashMap<>();
        List<String> wordList = new ArrayList<>(Arrays.asList(text.split(" "))).stream()
                .filter(str->str!=null&&!str.equals(""))
                .collect(Collectors.toList());

        // InvertedIndex 생성
        wordList.forEach(word->{
            word.length(); // 여기서 invertedIndex 생성 작업
        });

        processedData.values().forEach(invertedIndexRepository::save);
    }

}
