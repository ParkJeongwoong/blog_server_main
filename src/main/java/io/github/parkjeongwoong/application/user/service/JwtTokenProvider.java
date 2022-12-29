package io.github.parkjeongwoong.application.user.service;

import io.github.parkjeongwoong.application.user.dto.AccessJwtAuth;
import io.github.parkjeongwoong.application.user.dto.JwtAuth;
import io.github.parkjeongwoong.application.user.dto.RefreshJwtAuth;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    // 토큰 생성, 검증
    // 필터 클래스에서 사전 검증

    @Value("${jwtToken.key}")
    private String secretKey;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(JwtAuth jwtAuth, String requestType) {
        if (!requestType.equals("access") && !requestType.equals("refresh")) return null;
        long validTime = requestType.equals("access") ? 30 * 60 * 1000 : 3 * 60 * 60 * 1000;
        // JWT 토큰 정보 저장
        Claims claims = getClaims(jwtAuth, requestType);

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(claims)
                .setIssuedAt(new Date()) // 발행시간
                .setExpiration(new Date(System.currentTimeMillis() + validTime))
                .signWith(SignatureAlgorithm.HS256, secretKey) // (Header) alg, HS256 값 자동 세팅
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserName(token));
        return userDetails == null ? null : new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userName", String.class);
    }

    public String resolveToken(HttpServletRequest request, String requestType) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length>0) {
            for (Cookie cookie:cookies) {
                if (cookie.getName().equals(requestType)) {
                    return cookie.getValue();
                }
            }
        }
//        return request.getHeader(requestType);
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            log.info(e.toString());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    public long getRefreshTokenIdFromToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return (long) claims.getBody().get("refreshTokenId", Integer.class);
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        return header;
    }

    private Claims getClaims(JwtAuth jwtAuth, String requestType) {
        Claims claims = Jwts.claims().setSubject(jwtAuth.getUserId());
        if (requestType.equals("access")) { // 엑세스 토큰
            claims.put("roles", ((AccessJwtAuth) jwtAuth).getRoles());
            claims.put("userName", ((AccessJwtAuth) jwtAuth).getUserName());
            claims.put("refreshTokenId", ((AccessJwtAuth) jwtAuth).getRefreshTokenId());
        } else if (requestType.equals("refresh")) { // 리프레시 토큰
            claims.put("userEmail", ((RefreshJwtAuth) jwtAuth).getUserEmail());
        }
        return claims;
    }

}
