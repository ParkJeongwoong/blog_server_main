package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BlogRepositoryTest {

    @Autowired
    VisitorRepository visitorRepository;

    @AfterEach
    public void cleanup() { visitorRepository.deleteAll(); }

    @Test
    public void test_findAllByOrderByIdDesc() {
        // Given
        String url1 = "https://www.test.com";
        String ip1 = "127.0.0.1";
        boolean justVisited1 = true;

        String url2 = "https://github.com/ParkJeongwoong/";
        String ip2 = "127.0.0.1";
        String lastPage2 = "https://www.test.com";
        boolean justVisited2 = false;

        visitorRepository.save(Visitor.builder()
                .url(url1)
                .ip(ip1)
                .justVisited(justVisited1)
                .build());
        visitorRepository.save(Visitor.builder()
                .url(url2)
                .ip(ip2)
                .lastPage(lastPage2)
                .justVisited(justVisited2)
                .build());

        // When
        List<Visitor> visitorList = visitorRepository.findAllByOrderByIdDesc();

        // Then
        Visitor visitor1 = visitorList.get(0);
        Visitor visitor2 = visitorList.get(1);

        assertThat(visitor2.getUrl()).isEqualTo(url1);
        assertThat(visitor2.getIp()).isEqualTo(ip1);
        assertThat(visitor2.getLastPage()).isEqualTo(null);
        assertThat(visitor1.getUrl()).isEqualTo(url2);
        assertThat(visitor1.getIp()).isEqualTo(ip2);
        assertThat(visitor1.getLastPage()).isEqualTo(lastPage2);
    }
}
