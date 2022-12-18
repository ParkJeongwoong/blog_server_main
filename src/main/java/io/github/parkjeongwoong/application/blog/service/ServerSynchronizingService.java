package io.github.parkjeongwoong.application.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.parkjeongwoong.application.blog.dto.SendArticleSyncDto;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;
import io.github.parkjeongwoong.application.blog.usecase.ServerSynchronizingUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class ServerSynchronizingService implements ServerSynchronizingUsecase {
    private WebClient webClient;

    @Value("${backup.server}")
    String backupServer;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
    }

    public void visitSync(VisitorSaveRequestDto requestDto) {
        if (isBackupServerNotExist()) return;

        String BACKUP_URL = backupServer + "/blog-api/visited";
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonString = mapper.writeValueAsString(requestDto);

            webClient.post()
                    .uri(BACKUP_URL)
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Backup To : " + backupServer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendArticleSync(SendArticleSyncDto requestDto) {
        if (isBackupServerNotExist()) return;

        String BACKUP_URL = backupServer + "/blog-api/upload";
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonString = mapper.writeValueAsString(requestDto);

            webClient.post()
                    .uri(BACKUP_URL)
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Backup To : " + backupServer);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public void updateArticleSync() {

    }

    public void deleteArticleSync() {

    }

    private boolean isBackupServerNotExist() {
        return backupServer == null || backupServer.length() == 0;
    }
}
