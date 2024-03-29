package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.data.dto.SyncServerRequestDto;
import io.github.parkjeongwoong.application.data.usecase.ServerSynchronizingUsecase;
import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import io.github.parkjeongwoong.etc.ServerState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("data-api")
public class DataApiController {

    private final DataUsecase dataUsecase;
    private final ServerSynchronizingUsecase serverSynchronizingUsecase;

    @GetMapping("/download/{filename}")
    public void download(@PathVariable("filename") String filename, HttpServletResponse response) throws IOException {
        dataUsecase.downloadFile(filename, response);
    }

    @PostMapping("/sync")
    public void sync(@RequestBody SyncServerRequestDto requestDto, HttpServletResponse response) {
        serverSynchronizingUsecase.sync(requestDto, response);
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
