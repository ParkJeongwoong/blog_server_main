package io.github.parkjeongwoong.application.data.service;

import io.github.parkjeongwoong.application.data.usecase.DataUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataService implements DataUsecase {

    @Value("${download.path}")
    String DEFAULT_FILE_PATH;
    @Value("${backup.path}")
    String BACKUP_FILE_PATH;

    @Override
    public void downloadFile(String filename, HttpServletResponse response) throws IOException {

        if (filename == null || filename.equals("")) return ;
        File dFile = getFilePath(filename);
        download(dFile, response);

    }

    @Override
    public void downloadDumpFile(HttpServletResponse response) throws IOException {

        File dFile = getBackupFile();
        download(dFile, response);

    }

    private boolean backupDB(String dbUsername, String dbPassword, String dbName, String outputFile) throws IOException, InterruptedException {

        String command = String.format("mysqldump -u%s -p%s --add-drop-table --databases %s -r %s",
                dbUsername, dbPassword, dbName, outputFile);
        Process process = Runtime.getRuntime().exec(command);
        int processComplete = process.waitFor();
        return processComplete == 0;

    }

    private boolean restoreDB(String dbUsername, String dbPassword, String dbName, String sourceFile)
            throws IOException, InterruptedException {

        String[] command = new String[]{
                "mysql",
                "-u" + dbUsername,
                "-p" + dbPassword,
                "-e",
                " source " + sourceFile,
                dbName
        };
        Process runtimeProcess = Runtime.getRuntime().exec(command);
        int processComplete = runtimeProcess.waitFor();
        return processComplete == 0;

    }

    private File getFilePath(String filename) {
        String filePath = DEFAULT_FILE_PATH + filename;
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
        } else {log.info("File found");}

        return dFile;
    }

    private File getBackupFile() {
        String directoryPath = BACKUP_FILE_PATH;
        File directory = new File(directoryPath);
        FileFilter filter = pathname -> pathname.getName().startsWith("mariadb_")&&pathname.getName().endsWith("sql.tar.gz");
        File[] files = directory.listFiles(filter);
        return files[0];
    }

    private void download(File dFile, HttpServletResponse response) throws IOException {
        log.info("FILE NAME : {}", dFile.getName());
        log.info("FILE PATH : {}", dFile.getPath());
        long fSize = dFile.length();

        if (fSize > 0) {
            String encodedFilename = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode(dFile.getName(), "UTF-8");
            log.info("encodedFilename : {}", encodedFilename);
            setResponse(response, encodedFilename, fSize);
            bufferedStream(response, dFile);
        }
    }

    private void setResponse(HttpServletResponse response, String encodedFilename, long fSize) {
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", encodedFilename);
        response.setContentLengthLong(fSize);
    }

    private void bufferedStream(HttpServletResponse response, File dFile) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(dFile));
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());

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
