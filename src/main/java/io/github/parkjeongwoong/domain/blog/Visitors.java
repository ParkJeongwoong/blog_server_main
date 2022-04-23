package io.github.parkjeongwoong.domain.blog;

import io.github.parkjeongwoong.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(length = 15, nullable = true)
    private String ip;

    @Column(length = 100, nullable = true)
    private String lastPage;

    @Column(nullable = false)
    private boolean justVisited;

    @Builder
    public Visitors(String url, String ip, String lastPage, Boolean justVisited) {
        this.url = url;
        this.ip = ip;
        this.lastPage = lastPage;
        this.justVisited = justVisited;
    }
}
