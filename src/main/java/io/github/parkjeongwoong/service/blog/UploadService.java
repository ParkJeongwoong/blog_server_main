package io.github.parkjeongwoong.service.blog;

import io.github.parkjeongwoong.domain.blog.ArticleRepository;
import io.github.parkjeongwoong.web.dto.MarkdownSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UploadService {
    private final ArticleRepository articleRepository;

    @Transactional
    public Integer upload_markdown(MarkdownSaveRequestDto requestDto) {
        return check_image( articleRepository.save(requestDto.toEntity()).getContent() );
    }

    private Integer check_image(String articleData) {
        Pattern imagePattern = Pattern.compile("!\\[(.*?)]\\((?!http)(.*?)\\)");
        Matcher image_in_articleData = imagePattern.matcher(articleData);

        int cnt = 0;
        while (image_in_articleData.find())
            cnt++;

        return cnt;
    }
}
