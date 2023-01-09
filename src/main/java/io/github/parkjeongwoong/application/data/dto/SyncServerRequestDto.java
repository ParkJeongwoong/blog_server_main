package io.github.parkjeongwoong.application.data.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class SyncServerRequestDto {
    private String password;
    private String ip;

    private void setIp() {
        log.info("Get Sync request IP..");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-FORWARDED-FOR");
        log.info("Sync request IP - X-FORWARDED-FOR : {}", ip);
        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("Sync request IP - getRemoteAddr: {}", ip);
        }
        this.ip = ip;
    }

    public boolean checkSyncServer(String allowedIp, String allowedPassword) {
        setIp();
        return this.ip.equals(allowedIp) && this.password.equals(allowedPassword);
    }
}
