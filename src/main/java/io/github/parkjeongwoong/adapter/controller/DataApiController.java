package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import io.github.parkjeongwoong.etc.ServerState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("data-api")
public class DataApiController {

    private final DataUsecase dataUsecase;

    @GetMapping("/download/{filename}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("filename") String filename) throws IOException {
        dataUsecase.download(request, response, filename);
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
