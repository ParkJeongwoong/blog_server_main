package io.github.parkjeongwoong.application.data.service;

import io.github.parkjeongwoong.application.blog.dto.ArticleResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void setArticleOps(String key, ArticleResponseDto responseDto, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key,responseDto, ttl, unit);
    }

//    public Set<String>
//    // 참고 : https://co-de.tistory.com/14
}
