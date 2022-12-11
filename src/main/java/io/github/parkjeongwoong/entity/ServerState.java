package io.github.parkjeongwoong.entity;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerState {
    private long isArticleUpdate;

    @PostConstruct
    public void init() {
        this.isArticleUpdate = 0;
        System.out.println("ServerState init");
    }

    public long checkArticleUpdate() { return isArticleUpdate; }
    public void articleIsUpdated() { this.isArticleUpdate = 1; }
    public void postArticleUpdateProcessIsDone() { this.isArticleUpdate = 0; }
}
