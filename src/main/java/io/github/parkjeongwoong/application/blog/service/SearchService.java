package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.usecase.SearchUsecase;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService implements SearchUsecase {

    public void invertedIndexProcessing(String text) {
        Map<String, List<Long>> processedData = new HashMap<>();
        List<String> wordList = new ArrayList<>(Arrays.asList(text.split(" "))).stream()
                .filter(str->str!=null&&!str.equals(""))
                .collect(Collectors.toList());

    }

}
