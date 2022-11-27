package io.github.parkjeongwoong.application.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class ServerSynchronizingService {
    private WebClient webClient;

    @Value("${backup.server}")
    String backupServer;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
    }

    public void visitRequest(VisitorSaveRequestDto requestDto) {
        if (isBackupServerNotExist()) return;

        String BACKUP_URL = backupServer + "/blog-api/visited";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;

        try { jsonString = mapper.writeValueAsString(requestDto); }
        catch (JsonProcessingException e) { e.printStackTrace(); }

        String response = webClient.post()
                .uri(BACKUP_URL)
                .body(BodyInserters.fromValue(jsonString))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Backup To : " + backupServer);
    }

    public void saveArticleRequest() {

    }

    public void updateArticleRequest() {

    }

    public void deleteArticleRequest() {

    }

    private boolean isBackupServerNotExist() {
        return backupServer == null || backupServer.length() == 0;
    }
}
