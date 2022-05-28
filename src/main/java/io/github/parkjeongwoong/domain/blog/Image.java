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

    @Column(nullable = false)
    private Long article_id;

    @Column(length = 500, nullable = false)
    private String directory;

    @Builder
    Image(Long article_id, String directory) {
        this.article_id = article_id;
        this.directory = directory;
    }
}
