package io.github.parkjeongwoong.config.Auth;

import io.github.parkjeongwoong.application.user.dto.AccessJwtAuth;
import io.github.parkjeongwoong.application.user.service.JwtTokenProvider;
import io.github.parkjeongwoong.application.user.service.UserDeatilsService;
import io.github.parkjeongwoong.application.user.dto.JwtAuth;
import io.github.parkjeongwoong.entity.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    // JwtTokenProvider의 검증 이후 Jwt로 유저 정보를 조회 -> UserPasswordAuthenticationFilter로 전달

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDeatilsService userDeatilsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.resolveToken((HttpServletRequest) request, "AccessToken");

        if (accessToken != null) {
            try {
                if (jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException accessTokenExpired) { // accessToken 만료
                long refreshTokenId = accessTokenExpired.getClaims().get("refreshTokenId", Integer.class); // ExpiredJwtException에서 가져올 때 Integer 클래스로 가져옴
                String refreshToken_server = userDeatilsService.getRefreshTokenById(refreshTokenId);
                String refreshToken_client = jwtTokenProvider.resolveToken((HttpServletRequest) request, "RefreshToken");
                try {
                    if (jwtTokenProvider.compareRefreshToken(refreshToken_client, refreshToken_server) && jwtTokenProvider.validateToken(refreshToken_client)) {
                        String userId = accessTokenExpired.getClaims().getSubject();
                        User user = userDeatilsService.getUser(userId);
                        JwtAuth jwtAuth = new AccessJwtAuth(userId,user.getUserType(),user.getUsername(),refreshTokenId);
                        String newAccessToken = jwtTokenProvider.createToken(jwtAuth, "access");
                        ((HttpServletResponse) response).setHeader("AccessToken", newAccessToken);
                        Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (ExpiredJwtException refreshTokenExpired) {
                    System.out.println("Refresh Token 만료");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
