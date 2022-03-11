package io.github.parkjeongwoong.domain.blog;

import io.github.parkjeongwoong.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Visitors extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = true)
    private String url;

    @Builder
    public Visitors(String url) {
        this.url = url;
    }
}
