package io.github.parkjeongwoong.application.data.usecase;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DataUsecase {
    void download(String filename, HttpServletResponse response) throws IOException;
    void backup(HttpServletResponse response) throws IOException;
}
