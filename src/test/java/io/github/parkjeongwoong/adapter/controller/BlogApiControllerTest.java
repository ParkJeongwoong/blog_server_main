package io.github.parkjeongwoong.adapter.controller;

import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.VisitorRepository;
import io.github.parkjeongwoong.application.blog.repository.ImageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BlogApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ImageRepository imageRepository;

    public void visitors_setup() {
        String url1 = "https://www.test.com";
        String ip1 = "127.0.0.1";
        boolean justVisited1 = true;

        String url2 = "https://github.com/ParkJeongwoong/";
        String ip2 = "127.0.0.1";
        String lastPage2 = "https://www.test.com";
        boolean justVisited2 = false;

    }

    @AfterEach
    public void tearDown() {
        visitorRepository.deleteAll();
        articleRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void test_visited() {
        // Given
    }
}
