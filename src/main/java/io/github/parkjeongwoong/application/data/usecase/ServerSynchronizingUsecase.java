package io.github.parkjeongwoong.application.data.usecase;

import io.github.parkjeongwoong.application.blog.dto.SendArticleSyncDto;
import io.github.parkjeongwoong.application.data.dto.SyncServerRequestDto;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;

import javax.servlet.http.HttpServletResponse;

public interface ServerSynchronizingUsecase {
    void initWebClient();
    void visitSync(VisitorSaveRequestDto requestDto);
    void sendArticleSync(SendArticleSyncDto requestDto);
    boolean sync(SyncServerRequestDto requestDto, HttpServletResponse response);
    void updateArticleSync();
    void deleteArticleSync();
}
