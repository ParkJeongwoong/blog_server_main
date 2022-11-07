package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.service.textRefine.TextRefining;
import io.github.parkjeongwoong.entity.Article;
import io.github.parkjeongwoong.entity.InvertedIndex;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class ArticleSearchResultDto implements Comparable<ArticleSearchResultDto> {
    private String title;
    private String category;
    private long categoryId;
    private String subCategory;
    private String content;
    private String date;
    private List<Boolean> matchWords = new ArrayList<>();
    private int matchCount = 0; // 삭제 예정
    private long priorityScore;

    public ArticleSearchResultDto(Article entity, Long priorityScore) {
        this.title = entity.getTitle();
        this.category = entity.getCategory();
        this.categoryId = entity.getCategoryId();
        this.subCategory = entity.getSubCategory();
        this.content = entity.getContent();
        this.date = entity.getDate();
        this.priorityScore = priorityScore;
    }

    public void findWord(String[] words) {
//        Arrays.stream(words).forEach(this::matchingWord); // 삭제 예쩡
        this.content = TextRefining.preprocessingContent(this.content);

        if (this.content.length() > 300) {
            String lowercase_content = this.content.toLowerCase(Locale.ROOT);
            List<Integer> indexes = Arrays.stream(words).map(lowercase_content::indexOf).filter(index->!index.equals(-1)).sorted().collect(Collectors.toList());
            int start_index = indexes.get(0);
            int blankCount = 0;

            while (start_index > 0) {
                if (content.charAt(start_index-1) == ' ') {
                    blankCount += 1;
                    if (blankCount == 2)
                        break;
                }
                start_index -= 1;
            }

            if (start_index+300 > content.length()) {
                this.content = "...@$_%!^" + content.substring(content.length()-300);
            }
            else {
                this.content = content.substring(start_index, start_index+300) + "...@$_%!^";
            }
        }

        this.content = TextRefining.postprocessingContent(this.content);

        // 일치하는 단어 찾아서 True 설정
        for (int i=0;i<this.content.length();i++)
            this.matchWords.add(false);
        Arrays.stream(words).forEach(this::findIndexes);
    }

    private void findIndexes(String word) {
        int index = this.content.indexOf(word);

        while(index != -1) {
            for (int i=0;i<word.length();i++) {
                this.matchWords.set(index+i, true);
            }
            index = this.content.indexOf(word, index+word.length());
        }
    }

    // 삭제 예정
    private void matchingWord(String word) {
        int index_title = this.title.indexOf(word);
        int index = this.content.indexOf(word);

        while (index_title != -1) {
            this.matchCount += 5;
            index_title = this.title.indexOf(word, index_title+word.length());
        }
        while(index != -1) {
            this.matchCount += 1;
            index = this.content.indexOf(word, index+word.length());
        }

    }

    // 삭제 예정
    @Override
    public int compareTo(ArticleSearchResultDto articleSearchResultDto) {
        return articleSearchResultDto.getMatchCount() - this.matchCount;
    }
}
