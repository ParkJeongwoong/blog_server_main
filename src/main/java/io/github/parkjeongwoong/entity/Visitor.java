package io.github.parkjeongwoong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Visitor extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String url;

    @Column(length = 15)
    private String ip;

    @Column(length = 100)
    private String lastPage;

    @Column(nullable = false)
    private boolean justVisited;

    @Builder
    public Visitor(String url, String ip, String lastPage, Boolean justVisited) {
        this.url = url;
        this.ip = ip;
        this.lastPage = lastPage;
        this.justVisited = justVisited;
    }
}
