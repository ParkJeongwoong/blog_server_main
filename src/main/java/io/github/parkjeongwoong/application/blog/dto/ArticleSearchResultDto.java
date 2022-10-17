package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ArticleSearchResultDto {
    private String title;
    private String category;
    private long categoryId;
    private String subCategory;
    private String content;
    private String date;

    public ArticleSearchResultDto(Article entity) {
        this.title = entity.getTitle();
        this.category = entity.getCategory();
        this.categoryId = entity.getCategoryId();
        this.subCategory = entity.getSubCategory();
        this.content = entity.getContent();
        this.date = entity.getDate();
    }

    public void findWord(String[] words) {
        if (this.content.length() > 300) {
            List<Integer> indexes = Arrays.stream(words).map(word->content.indexOf(word)).filter(index->!index.equals(-1)).sorted().collect(Collectors.toList());
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
                this.content = "...@$_%!^" + content.substring(content.length()-300).trim();
            }
            else {
                this.content = content.substring(start_index, start_index+300) + "...@$_%!^";
            }
        }

        refineContent();
    }

    private void refineContent() {
        this.content = content.replaceAll("#"," ")
                .replaceAll("\n"," ")
                .replaceAll("\t", " ")
                .replaceAll("!\\[(.*?)\\.\\.\\.@\\$_%!\\^","[이미지]...")
                .replaceAll("]\\((.*?)\\.\\.\\.@\\$_%!\\^","]...")
                .replaceAll("!\\[(.*?)]\\((.*?)\\)","[이미지]")
                .replaceAll("]\\((.*?)\\)","]")
                .replace("@$_%!^","")
                .trim();
    }
}
