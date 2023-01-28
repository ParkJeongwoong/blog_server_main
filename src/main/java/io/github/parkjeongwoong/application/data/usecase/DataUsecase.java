package io.github.parkjeongwoong.application.data.usecase;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DataUsecase {
    void downloadFile(String filename, HttpServletResponse response) throws IOException;
    void downloadDumpFile(HttpServletResponse response) throws IOException;
}
