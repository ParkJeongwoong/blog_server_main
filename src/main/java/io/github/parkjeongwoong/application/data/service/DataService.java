package io.github.parkjeongwoong.application.data.service;

import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RequiredArgsConstructor
@Service
public class DataService implements DataUsecase {
    @Value("${download.path}")
    String filePath;

    // 출처 : https://kitty-geno.tistory.com/105
    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String filename) throws IOException {
        if (filename == null || filename.equals("")) {
            return ;
        }

        System.out.println(filePath);
        System.out.println(filename);
        File dFile = new File(filePath, filename);

        int fSize = (int) dFile.length();
        System.out.println(fSize);

        if (fSize > 0) {
            System.out.println("0");
            String encodedFilename = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode(filename, "UTF-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Dispotition", encodedFilename);
            response.setContentLengthLong(fSize);

            BufferedInputStream in;
            BufferedOutputStream out;

            in = new BufferedInputStream(new FileInputStream(dFile));
            out = new BufferedOutputStream(response.getOutputStream());

            try {
                System.out.println("1");
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("2");

                out.flush();
            } finally {
                System.out.println("3");
                in.close();
                out.close();
            }
        }

    }
}
