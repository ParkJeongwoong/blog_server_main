package io.github.parkjeongwoong.application.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.parkjeongwoong.application.blog.dto.MailSendDto;
import io.github.parkjeongwoong.application.blog.dto.SendArticleSyncDto;
import io.github.parkjeongwoong.application.data.dto.SyncServerRequestDto;
import io.github.parkjeongwoong.application.blog.dto.VisitorSaveRequestDto;
import io.github.parkjeongwoong.application.blog.usecase.MailingUsecase;
import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import io.github.parkjeongwoong.application.data.usecase.ServerSynchronizingUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServerSynchronizingService implements ServerSynchronizingUsecase {

    private final MailingUsecase mailingUsecase;
    private final DataUsecase dataUsecase;
    private WebClient webClient;

    @Value("${backup.server}")
    String backupServer;
    @Value("${backup.db.sub.ip}")
    String subServerIp;
    @Value("${backup.db.sub.pw}")
    String subServerPw;

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

            log.info("Backup To : {}", backupServer);
        } catch (JsonProcessingException e) {
            log.error("Visit Sync Error", e);
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

            log.info("Backup To : {}", backupServer);
        } catch (JsonProcessingException e) {
            log.error("Send Article Sync Error", e);
        }

    }

    @Override
    public void sync(SyncServerRequestDto requestDto, HttpServletResponse response) {
        if (requestDto.checkSyncServer(subServerIp, subServerPw)) {
            try {
                dataUsecase.downloadDumpFile(response);
            } catch (IOException e) {
                log.error("Sub Server Synchronizing Error", e);
            }
        } else {
            log.warn("검증/등록된 Sub Server가 아닙니다.");
        }
    }

    public void updateArticleSync() {

    }

    public void deleteArticleSync() {

    }

    private boolean isBackupServerNotExist() {
        return backupServer == null || backupServer.length() == 0;
    }

//    @Scheduled(cron = "0 0 * * * *")
//    private void subServerHealthCheck() {
//        log.info("Sub Server Ping Check");
//        if (ping("sub")) {
//            log.info("Sub Server is Good!");
//        } else {
//            log.warn("WARNING!! Your Back-up Server doesn't respond. Check Your Server Status!");
//            log.warn("If you haven't received any email, check Main Server Error Mail Function.");
//        }
//    }

    private boolean ping(String address) {

        try {

            if (address.equals("sub")) address = subServerIp;
            InetAddress inetAddress = InetAddress.getByName(address);

            if (inetAddress.isReachable(2000)) {
                log.info("Reachable IP : {}", address);
                return true;
            } else {
                log.info("Unreachable IP : {}", address);
                sendSubServerPingErrorMail();
            }

        } catch (UnknownHostException e) {
            log.error("IP 주소 문제", e);
        } catch (IOException e) {
            log.error("isReachable 문제", e);
        }

        return false;

    }

    private void sendSubServerPingErrorMail() {
        log.warn("Sub Server Ping Error 발생!!");
        MailSendDto mailSendDto = makeSubServerPingErrorMail();
        mailingUsecase.sendMail(mailSendDto);
    }

    private MailSendDto makeSubServerPingErrorMail() {
        return MailSendDto.builder()
                .address("dvlprjw@gmail.com")
                .title("[Woong's Blog] Sub Server - Ping Error")
                .content("Sub Server Ping Error occurred. Check your blog's sub server.")
                .build();
    }

}
