package io.github.parkjeongwoong.application.blog.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorSaveRequestDtoTest {

    @Autowired
    MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        request = new MockHttpServletRequest();
        request.addHeader("X-FORWARDED-FOR", "127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void test_setData() throws Exception {
        // Given
        String testUrl1 = "https://www.test.com";
        String testUrl2 = "https://github.com/ParkJeongwoong/";
        VisitorSaveRequestDto requestDto1 = VisitorSaveRequestDto.builder()
                .url(testUrl1)
                .build();
        VisitorSaveRequestDto requestDto2 = VisitorSaveRequestDto.builder()
                .url(testUrl2)
                .lastPage(testUrl1)
                .build();

        // When
        requestDto1.setData();
        requestDto2.setData();

        // Then
        assertThat(requestDto1.getIp()).isEqualTo("127.0.0.1");
        assertThat(requestDto1.getUrl()).isEqualTo(testUrl1);
        assertThat(requestDto1.getLastPage()).isEqualTo(null);
        assertThat(requestDto1.getJustVisited()).isEqualTo(true);
        assertThat(requestDto2.getIp()).isEqualTo("127.0.0.1");
        assertThat(requestDto2.getUrl()).isEqualTo(testUrl2);
        assertThat(requestDto2.getLastPage()).isEqualTo(testUrl1);
        assertThat(requestDto2.getJustVisited()).isEqualTo(false);
    }
}
