package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
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
    private long matchCount = 0;

    public ArticleSearchResultDto(Article entity) {
        this.title = entity.getTitle();
        this.category = entity.getCategory();
        this.categoryId = entity.getCategoryId();
        this.subCategory = entity.getSubCategory();
        this.content = entity.getContent();
        this.date = entity.getDate();
    }

    public void findWord(String[] words) {
        preprocessContent();

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

        refineContent();

        // 일치하는 단어 찾아서 True 설정
        for (int i=0;i<this.content.length();i++)
            this.matchWords.add(false);
        Arrays.stream(words).forEach(this::findIndexes);
    }

    private void preprocessContent() {
        this.content = content.replaceAll("#","")
                .replaceAll("\n"," ")
                .replaceAll("\t", " ")
                .replaceAll("\\*", "")
                .replaceAll("---", "")
                .replaceAll("`", "")
                .replaceAll("<u>", "")
                .replaceAll("</u>", "")
                .replaceAll("\\|", "")
                .replaceAll(":", "")
                .replaceAll("<br>", "")
                .replaceAll("!\\[(.*?)]\\((.*?)\\)","[이미지]")
                .replaceAll("]\\((.*?)\\)","]")
                .trim();
    }

    private void refineContent() {
        this.content = content.replaceAll("!\\[(.*?)\\.\\.\\.@\\$_%!\\^","[이미지]...")
                .replaceAll("]\\((.*?)\\.\\.\\.@\\$_%!\\^","]...")
                .replaceAll("!\\[(.*?)]\\((.*?)\\)","[이미지]")
                .replaceAll("]\\((.*?)\\)","]")
                .replace("@$_%!^","")
                .trim();
    }

    private void findIndexes(String word) {
        int index = this.content.indexOf(word);
        this.matchCount += 1;

        while(index != -1) {
            for (int i=0;i<word.length();i++) {
                this.matchWords.set(index+i, true);
            }
            index = this.content.indexOf(word, index+word.length());
            this.matchCount += 1;
        }
    }

    @Override
    public int compareTo(ArticleSearchResultDto articleSearchResultDto) {
        if (this.matchCount < articleSearchResultDto.matchCount)
            return 1;
        else if (this.matchCount > articleSearchResultDto.matchCount)
            return -1;
        return 0;
    }
}
