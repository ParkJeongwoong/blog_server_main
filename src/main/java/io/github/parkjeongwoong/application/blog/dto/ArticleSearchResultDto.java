package io.github.parkjeongwoong.application.blog.dto;

import io.github.parkjeongwoong.entity.Article;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    private List<Boolean> matchWords = new ArrayList<>();

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
            System.out.println(title);
            System.out.println("aaaa");
            List<Integer> indexes = Arrays.stream(words).map(word->content.indexOf(word)).filter(index->!index.equals(-1)).sorted().collect(Collectors.toList());
            System.out.println("bbbb");
            System.out.println(indexes.toString());
            int start_index = indexes.get(0);
            System.out.println("cccc");
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

        System.out.println(category + categoryId+ title);
        // 일치하는 단어 찾아서 True 설정
        for (int i=0;i<this.content.length();i++)
            this.matchWords.add(false);
        Arrays.stream(words).forEach(this::findIndexes);
        System.out.println("55555555555");
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
        System.out.println("1");
        int index = this.content.indexOf(word);
        System.out.println("2");

        while(index != -1) {
            System.out.println("index : " + index);
            for (int i=0;i<word.length();i++) {
                this.matchWords.set(index+i, true);
            }
            index = this.content.indexOf(word, index+word.length());
            System.out.println("3");
        }
        System.out.println("4");
    }
}
