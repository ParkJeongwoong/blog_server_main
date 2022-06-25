package io.github.parkjeongwoong.service.blog;

import io.github.parkjeongwoong.domain.blog.BlogRepository;
import io.github.parkjeongwoong.web.dto.PageVisitorsListResponseDto;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional
    public void visited(VisitorsSaveRequestDto requestDto) {
        System.out.println("Visitor just visited : " + requestDto.getUrl());
        System.out.println("Visitor's IP address is : " + requestDto.getIp());
        System.out.println("Current Time : " + new Date());

        // 구글 봇 (66.249.~) 와 내 ip (58.140.57.190) 제외
        if (isRecordable(requestDto.getIp())) {
            return ;
        }
        blogRepository.save(requestDto.toEntity());
    }

    @Transactional
    public long countVisitors() {
        return blogRepository.findAllDesc().size();
    }

    @Transactional(readOnly = true)
    public List<VisitorsListResponseDto> history() {
        return blogRepository.findAllDesc().stream()
                .map(VisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_page() {
        return blogRepository.countVisitors_page().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PageVisitorsListResponseDto> countVisitors_firstPage() {
        return blogRepository.countVisitors_firstPage().stream()
                .map(PageVisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }

    private Boolean isRecordable(String ip) {
        String[] notRecordableArray = {"58.140.57.190" // 공덕 ip
                                 , "118.221.44.132" // 양평동 ip1
                                 , "39.115.83.55" // 양평동 ip2
                                 , "222.110.245.239" // 키움증권 ip
                                 , "0:0:0:0:0:0:0:1"}; // local test ip
        ArrayList<String> notRecordableList = new ArrayList<>(Arrays.asList(notRecordableArray));
        return notRecordableList.contains(ip) || Objects.equals(ip.substring(0,6), "66.249"); // 구글 봇
    }
}
