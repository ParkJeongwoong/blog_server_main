package io.github.parkjeongwoong.application.data.service;

import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataService implements DataUsecase {
    @Value("${download.path}")
    String default_filePath;

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String filename) throws IOException {
        if (filename == null || filename.equals("")) {
            return ;
        }

        File dFile = getFilePath(filename);

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

    public boolean backupDB(String dbUsername, String dbPassword, String dbName, String outputFile) throws IOException, InterruptedException {

        String command = String.format("mysqldump -u %s -p %s --add-drop-table --databases %s -r %s",
                dbUsername, dbPassword, dbName, outputFile);
        Process process = Runtime.getRuntime().exec(command);
        int processComplete = process.waitFor();
        return processComplete == 0;

    }

    public boolean restoreDB(String dbUsername, String dbPassword, String dbName, String sourceFile)
            throws IOException, InterruptedException {

        String[] command = new String[]{
                "mysql",
                "-u " + dbUsername,
                "-p " + dbPassword,
                "-e",
                " source " + sourceFile,
                dbName
        };
        Process runtimeProcess = Runtime.getRuntime().exec(command);
        int processComplete = runtimeProcess.waitFor();
        return processComplete == 0;

    }

    private File getFilePath(String filename) {
        String filePath = default_filePath + filename;
        File dFile = new File(filePath);
        if (!dFile.exists()) {
            log.info("Find file again");

            filePath = System.getProperty("user.dir")
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "resources"
                    + File.separator + "downloadable"
                    + File.separator + filename;
            dFile = new File(filePath);
        } else {log.info("Find file");}

        log.info("File Path : {}", filePath);
        return dFile;
    }

}
