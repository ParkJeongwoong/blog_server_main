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

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Column(length = 8, nullable = true)
    private String date;

    @Column(length = 50, nullable = false)
    private String fileName;

    @Builder Article(String title, String content, String date, String fileName) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.fileName = fileName;
    }
}
