package io.github.parkjeongwoong.etc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ServerState {
    private long isArticleUpdate;

    @PostConstruct
    public void init() {
        this.isArticleUpdate = 0;
        log.info("ServerState init");
    }

    public long checkArticleUpdate() { return isArticleUpdate; }
    public void articleIsUpdated() { this.isArticleUpdate = 1; }
    public void postArticleUpdateProcessIsDone() { this.isArticleUpdate = 0; }
}
