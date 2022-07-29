package io.github.parkjeongwoong.domain.blog;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column
    private Long categoryId;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(length = 20)
    private String subCategory;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Column(length = 8)
    private String date;

    @Column(length = 50, nullable = false)
    private String fileName;

    @Builder Article(String title, String content, String date, String fileName, Long categoryId, String category, String subCategory) {
        this.title = title;
        this.categoryId = categoryId;
        this.category = category;
        this.subCategory = subCategory;
        this.content = content;
        this.date = date;
        this.fileName = fileName;
    }
}
