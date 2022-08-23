package io.github.parkjeongwoong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private long categoryId;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(length = 20)
    private String subCategory;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Column(length = 8)
    private String date;

    @Column(length = 100, nullable = false)
    private String fileName;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final List<Image> images = new ArrayList<>();

    @Builder Article(String title, String content, String date, String fileName, long categoryId, String category, String subCategory) {
        this.title = title;
        this.categoryId = categoryId;
        this.category = category;
        this.subCategory = subCategory;
        this.content = content;
        this.date = date;
        this.fileName = fileName;
    }

    public void update(String content) {
        this.content = content;
    }
}
