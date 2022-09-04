package io.github.parkjeongwoong.application.data.service;

import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RequiredArgsConstructor
@Service
public class DataService implements DataUsecase {
    @Value("${download.path}")
    String filePath;

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String filename) throws IOException {
        if (filename == null || filename.equals("")) {
            return ;
        }

        File dFile = new File(filePath, filename);
        if (!dFile.exists()) {
            filePath = System.getProperty("user.dir")
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "resources"
                    + File.separator + "downloadable"
                    + File.separator + filename;
            dFile = new File(filePath);
        }

        System.out.println(filename);
        System.out.println(filePath);

        int fSize = (int) dFile.length();

        if (fSize > 0) {
            String encodedFilename = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode(filename, "UTF-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Dispotition", encodedFilename);
            response.setContentLengthLong(fSize);

            BufferedInputStream in;
            BufferedOutputStream out;

            in = new BufferedInputStream(new FileInputStream(dFile));
            out = new BufferedOutputStream(response.getOutputStream());

            try {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush();
            } finally {
                in.close();
                out.close();
            }
        }

    }
}
