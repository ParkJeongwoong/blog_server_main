package io.github.parkjeongwoong.adapter.filter;

import io.github.parkjeongwoong.application.user.dto.AccessJwtAuth;
import io.github.parkjeongwoong.application.user.service.JwtTokenProvider;
import io.github.parkjeongwoong.application.user.service.UserDetailsService;
import io.github.parkjeongwoong.application.user.dto.JwtAuth;
import io.github.parkjeongwoong.entity.user.RefreshToken;
import io.github.parkjeongwoong.entity.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.resolveToken((HttpServletRequest) request, "accessToken");

        if (accessToken != null) {
            try {
                if (jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    if (authentication == null) {
                        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                                .secure(true)
                                .maxAge(0)
                                .path("/") // 쿠키가 저장되는 페이지
                                .sameSite("Lax")
                                .httpOnly(true)
                                .build();
                        ((HttpServletResponse) response).addHeader("Set-Cookie", cookie.toString());
                    }
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    ((HttpServletResponse) response).setStatus(200);
                }
            } catch (ExpiredJwtException accessTokenExpired) { // accessToken 만료
                long refreshTokenId = accessTokenExpired.getClaims().get("refreshTokenId", Integer.class); // ExpiredJwtException에서 가져올 때 Integer 클래스로 가져옴
                RefreshToken refreshToken = userDetailsService.getRefreshTokenById(refreshTokenId);
                try {
                    if (refreshToken.isAvailable() && jwtTokenProvider.validateToken(refreshToken.getValue())) {
                        String userId = accessTokenExpired.getClaims().getSubject();
                        User user = userDetailsService.getUser(userId);
                        JwtAuth jwtAuth = new AccessJwtAuth(userId,user.getUserType(),user.getUsername(),refreshTokenId);
                        String newAccessToken = jwtTokenProvider.createToken(jwtAuth, "access");
                        ((HttpServletResponse) response).setHeader("AccessToken", newAccessToken);
                        Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        ((HttpServletResponse) response).setStatus(206);
                    }
                } catch (ExpiredJwtException refreshTokenExpired) { // refreshToken 만료
                    System.out.println("Refresh Token 만료");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
