package io.github.parkjeongwoong.application.blog.usecase;

import io.github.parkjeongwoong.application.blog.dto.SendArticleSyncDto;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;

public interface ServerSynchronizingUsecase {
    void initWebClient();
    void visitSync(VisitorSaveRequestDto requestDto);
    void sendArticleSync(SendArticleSyncDto requestDto);
    void updateArticleSync();
    void deleteArticleSync();
}
