package io.github.parkjeongwoong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 생성, 수정 시간 기록을 위한 JPA Auditing 활성화
@SpringBootApplication
public class Application {
    public  static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
