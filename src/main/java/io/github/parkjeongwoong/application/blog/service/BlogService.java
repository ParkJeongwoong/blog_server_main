package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.application.blog.repository.ArticleRepository;
import io.github.parkjeongwoong.application.blog.repository.VisitorRepository;
import io.github.parkjeongwoong.application.blog.usecase.BlogUsecase;
import io.github.parkjeongwoong.entity.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService implements BlogUsecase {
    private final VisitorRepository visitorRepository;
    private final ArticleRepository articleRepository;
    private final ServerSynchronizingService serverSynchronizingService;

    @Autowired
    private final RedisTemplate redisTemplate;

    public void visited(VisitorSaveRequestDto requestDto) {
        Visitor visitor = requestDto.toEntity();
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Visitor just visited : " + visitor.getUrl());
        System.out.println("Visitor's IP address is : " + visitor.getIp());
        System.out.println("Current Time : " + currentTime);

//        if (isRecordable(visitor.getIp())) return ; // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
        if (isStrangeAccess(visitor.getIp(), currentTime)) return ;
        visitorRepository.save(visitor);
//        serverSynchronizingService.visitSync(requestDto);// Backup
    }

    @Transactional
    public long countVisitor() {
        return visitorRepository.count();
    }

    @Transactional(readOnly = true)
    public List<VisitorResponseDto> history() {
        return visitorRepository.findAllByOrderByIdDesc().stream()
                .map(VisitorResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorResponseDto> countVisitor_page() {
        return visitorRepository.countVisitor_page().stream()
                .map(PageVisitorResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorResponseDto> countVisitor_firstPage() {
        return visitorRepository.countVisitor_firstPage().stream()
                .map(PageVisitorResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<DailyVisitorResponseDto> countDailyVisitor() {
        return visitorRepository.countDailyVisitor().stream()
                .map(DailyVisitorResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<VisitorCountResponseDto> countVisitorRank() {
        return visitorRepository.countVisitor().stream()
                .map(VisitorCountResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<VisitorCountResponseDto> countVisitorRank_date(String startDate, String endDate) {
        return visitorRepository.countVisitor_date(startDate, endDate+"T23:59:59.99").stream()
                .map(VisitorCountResponseDto::new)
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

    public List<VisitorTimelineResponseDto> getVisitorTimeline(String startDate, String endDate) {
        List<VisitorTimelineDto> timelineDtos = visitorRepository.getVisitorTimeline(startDate, endDate+"T23:59:59.99").stream()
                .map(VisitorTimelineDto::new)
                .collect(Collectors.toList());
        startDate = startDate.replaceAll("-","");
        endDate = endDate.replaceAll("-","");
        return makeTimelineResponse(timelineDtos
                , LocalDate.of(
                        Integer.parseInt(startDate.substring(0,4)),
                        Integer.parseInt(startDate.substring(4,6)),
                        Integer.parseInt(startDate.substring(6,8)))
                , LocalDate.of(
                        Integer.parseInt(endDate.substring(0,4)),
                        Integer.parseInt(endDate.substring(4,6)),
                        Integer.parseInt(endDate.substring(6,8))));
    }

    private boolean isRecordable(String ip) {
        String[] notRecordableArray = {"58.140.57.190" // 공덕 ip
                                 , "118.221.44.132" // 양평동 ip1
                                 , "39.115.83.55" // 양평동 ip2
                                 , "222.110.245.239"}; // 키움증권 ip
        ArrayList<String> notRecordableList = new ArrayList<>(Arrays.asList(notRecordableArray));
        return notRecordableList.contains(ip) || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }

    private boolean isStrangeAccess(String ip, LocalDateTime date) {
        Visitor lastVisitor = visitorRepository.findTop1ByOrderByIdDesc();
        if (lastVisitor == null || !Objects.equals(lastVisitor.getIp(), ip)) return false;
        Duration duration = Duration.between(lastVisitor.getCreatedDate(), date);
        if (duration.getSeconds() < 1 && duration.getNano() < 700000000) {
            System.out.println("Warning Too Fast Access! Duration : " + duration.getSeconds() + "." + String.format("%09d",duration.getNano()) + "s");
            return true;
        }
        return false;
    }

    private List<VisitorTimelineResponseDto> makeTimelineResponse(List<VisitorTimelineDto> timelineDtos, LocalDate startDate, LocalDate endDate) {
        if (timelineDtos.size()==0) return new ArrayList<>();
        List<VisitorTimelineResponseDto> responseDtos = new ArrayList<>();
        VisitorTimelineDto timelineDto;
        LocalDate date = null;
        int hour = 0;
        boolean getNewDtoTF = true;

        LocalDate firstDate = timelineDtos.get(timelineDtos.size()-1).getVisitedDate().toLocalDate();
        LocalDate currentDate = startDate;
        VisitorTimelineResponseDto timelineResponseDto = new VisitorTimelineResponseDto(currentDate, 0);

        while (currentDate.isBefore(firstDate)) {
            responseDtos.add(timelineResponseDto);
            int currentHour = timelineResponseDto.getHour()+1;
            if (currentHour > 24) {
                currentDate = currentDate.plusDays(1);
                currentHour = 0;
            }
            timelineResponseDto = new VisitorTimelineResponseDto(currentDate, currentHour);
        }
        while (timelineDtos.size() > 0) {
            if (getNewDtoTF) {
                timelineDto = timelineDtos.remove(timelineDtos.size()-1);
                date = timelineDto.getVisitedDate().toLocalDate();
                hour = timelineDto.getVisitedDate().getHour();
                getNewDtoTF = false;
            }

            if (date.isEqual(timelineResponseDto.getDate()) && hour == timelineResponseDto.getHour()) {
                timelineResponseDto.addCount();
                getNewDtoTF = true;
            }
            else {
                responseDtos.add(timelineResponseDto);
                int currentHour = timelineResponseDto.getHour()+1;
                if (currentHour == 24) {
                    currentDate = currentDate.plusDays(1);
                    currentHour = 0;
                }
                timelineResponseDto = new VisitorTimelineResponseDto(currentDate, currentHour);
            }
        }
        while (true) {
            responseDtos.add(timelineResponseDto);
            int currentHour = timelineResponseDto.getHour()+1;
            if (currentHour > 24) {
                currentDate = currentDate.plusDays(1);
                currentHour = 0;
            }
            if (currentDate.isAfter(endDate)) break;
            timelineResponseDto = new VisitorTimelineResponseDto(currentDate, currentHour);
        }
        return responseDtos;
    }
}
