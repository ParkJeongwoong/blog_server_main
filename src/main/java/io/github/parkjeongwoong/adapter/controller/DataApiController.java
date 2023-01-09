package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.data.dto.SyncServerRequestDto;
import io.github.parkjeongwoong.application.data.usecase.ServerSynchronizingUsecase;
import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import io.github.parkjeongwoong.etc.ServerState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("data-api")
public class DataApiController {

    private final DataUsecase dataUsecase;
    private final ServerSynchronizingUsecase serverSynchronizingUsecase;

    @GetMapping("/download/{filename}")
    public void download(HttpServletResponse response, @PathVariable("filename") String filename) throws IOException {
        dataUsecase.download(filename, response);
    }

    @GetMapping("/ping")
    public boolean ping() {
        return serverSynchronizingUsecase.ping("sub");
    }

    @PostMapping("/sync")
    public boolean sync(@RequestBody SyncServerRequestDto requestDto, HttpServletResponse response) {
        return serverSynchronizingUsecase.sync(requestDto, response);
    }
    @GetMapping("/sync")
    public boolean sync2(HttpServletResponse response) {
        return serverSynchronizingUsecase.sync(null, response);
    }

    // 전역 변수 TEST
    @Autowired
    private ServerState serverState;
    @GetMapping("/checkUpdate")
    public long checkUpdate() { return serverState.checkArticleUpdate(); }
    @GetMapping("/setUpdate")
    public void setUpdate() { serverState.articleIsUpdated(); }
    @GetMapping("/finishUpdate")
    public void finishUpdate() { serverState.postArticleUpdateProcessIsDone(); }

}
