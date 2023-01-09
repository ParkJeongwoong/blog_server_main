package io.github.parkjeongwoong.application.data.usecase;

import io.github.parkjeongwoong.application.blog.dto.SendArticleSyncDto;
import io.github.parkjeongwoong.application.data.dto.SyncServerRequestDto;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;

public interface ServerSynchronizingUsecase {
    void initWebClient();
    void visitSync(VisitorSaveRequestDto requestDto);
    void sendArticleSync(SendArticleSyncDto requestDto);
    boolean ping(String address);
    boolean sync(SyncServerRequestDto requestDto);
    void updateArticleSync();
    void deleteArticleSync();
}
