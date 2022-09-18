package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.VisitorRepository;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import io.github.parkjeongwoong.entity.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService implements BlogUsecase {
    private final VisitorRepository visitorRepository;
    private final ArticleRepository articleRepository;
    private WebClient webClient;

    @Value("${backup.server}")
    String backupServer;
    @Autowired
    private final RedisTemplate redisTemplate;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.create(backupServer);
    }

    @Transactional
    public void visited(VisitorSaveRequestDto requestDto) {
        Visitor visitor = requestDto.toEntity();
        visitor.setData();
        System.out.println("Visitor just visited : " + visitor.getUrl());
        System.out.println("Visitor's IP address is : " + visitor.getIp());
        System.out.println("Current Time : " + new Date());

        if (isRecordable(visitor.getIp())) return ; // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
        visitorRepository.save(visitor);

        // Backup
        if (backupServer != null && backupServer.length() != 0) {
            String backupUrl = backupServer + "/blog-api/visited";
            MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
            httpBody.add("url", requestDto.getUrl());
            httpBody.add("lastPage", requestDto.getLastPage());
            String response = webClient.post()
                            .uri("/blog-api/visited")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(httpBody))
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
            System.out.println("Backup Result : " + response);
        }
    }

    @Transactional
    public long countVisitor() {
        return visitorRepository.count();
    }

    @Transactional(readOnly = true)
    public List<VisitorListResponseDto> history() {
        return visitorRepository.findAllByOrderByIdDesc().stream()
                .map(VisitorListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorListResponseDto> countVisitor_page() {
        return visitorRepository.countVisitor_page().stream()
                .map(PageVisitorListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorListResponseDto> countVisitor_firstPage() {
        return visitorRepository.countVisitor_firstPage().stream()
                .map(PageVisitorListResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseDto> getArticleList() {
        return articleRepository.findAllDesc().stream()
                .map(ArticleResponseDto::new)
                .collect(Collectors.toList());
    }

    public ArticleResponseDto getArticle(String category, Long categoryId) {
        ValueOperations<String, ArticleResponseDto> valueOperations = redisTemplate.opsForValue();
        String redis_key = "a"+category+categoryId;
        ArticleResponseDto article = valueOperations.get(redis_key);
        if (article == null) {
            article = articleRepository.findByCategoryAndId(category, categoryId);
            valueOperations.set(redis_key, article, 7, TimeUnit.DAYS);
        }
        return article;
    }

    public byte[] getImage(String imageName) throws IOException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String[] imagePath_split = imageName.split("/");
        String imagePath = String.join(File.separator, imagePath_split);
        String redis_key = "i"+imagePath;
        String image_string = valueOperations.get(redis_key);
        if (image_string == null || image_string.length() == 0) {
            InputStream imageStream = new FileInputStream(System.getProperty("user.dir")
                    + File.separator + "src"
                    + File.separator + "main"
                    + File.separator + "resources"
                    + File.separator + "article_images"
                    + File.separator + imageName);
            byte[] image = StreamUtils.copyToByteArray(imageStream);
            image_string = Base64.getEncoder().encodeToString(image);
            imageStream.close();
            valueOperations.set(redis_key, image_string, 3, TimeUnit.DAYS);
        }
        return Base64.getDecoder().decode(image_string);
    }

    private boolean isRecordable(String ip) {
        String[] notRecordableArray = {"58.140.57.190" // 공덕 ip
                                 , "118.221.44.132" // 양평동 ip1
                                 , "39.115.83.55" // 양평동 ip2
                                 , "222.110.245.239"}; // 키움증권 ip
        ArrayList<String> notRecordableList = new ArrayList<>(Arrays.asList(notRecordableArray));
        return notRecordableList.contains(ip) || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }
}
