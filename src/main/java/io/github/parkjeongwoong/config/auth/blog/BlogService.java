package io.github.parkjeongwoong.config.auth.blog;

import io.github.parkjeongwoong.domain.blog.BlogRepository;
import io.github.parkjeongwoong.web.dto.VisitorsSaveRequestDto;
import io.github.parkjeongwoong.web.dto.VisitorsListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional
    public Long visited(VisitorsSaveRequestDto requestDto) {
        System.out.println("Visitor just visited : " + requestDto.getUrl());
        return blogRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public long countVisitors() {
        return blogRepository.findAllDesc().stream().count();
    }

    @Transactional(readOnly = true)
    public List<VisitorsListResponseDto> history() {
        return blogRepository.findAllDesc().stream()
                .map(VisitorsListResponseDto::new)
                .collect(Collectors.toList());
    }
}
