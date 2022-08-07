package io.github.parkjeongwoong.domain.blog;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(length = 500, nullable = false)
    private String directory;

    @Builder
    Image(Article article, String directory) {
        this.article = article;
        this.directory = directory;
    }
}
