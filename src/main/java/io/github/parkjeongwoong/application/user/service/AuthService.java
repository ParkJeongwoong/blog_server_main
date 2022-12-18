package io.github.parkjeongwoong.application.user.service;

import io.github.parkjeongwoong.application.user.dto.*;
import io.github.parkjeongwoong.application.user.repository.RefreshTokenRepository;
import io.github.parkjeongwoong.entity.user.RefreshToken;
import io.github.parkjeongwoong.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDeatilsService userDeatilsService;

    public UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            User user = userDeatilsService.getUser(requestDto.getUserId());
            boolean result = user.getEncryptedPassword().checkPassword(requestDto.getPassword());
            if (result) {
                log.info("로그인 성공");
                RefreshJwtAuth refreshJwtAuth = new RefreshJwtAuth(user.getUserId(),user.getEmail());
                String refreshToken = jwtTokenProvider.createToken(refreshJwtAuth, "refresh");
                RefreshToken refreshTokenEntity = RefreshToken.builder().userId(user.getUserId()).userEmail(user.getEmail()).value(refreshToken).build();
                long refreshTokenId = refreshTokenRepository.save(refreshTokenEntity).getId();
                AccessJwtAuth accessJwtAuth = new  AccessJwtAuth(user.getUserId(),user.getUserType(),user.getUsername(),refreshTokenId);
                String accessToken = jwtTokenProvider.createToken(accessJwtAuth, "access");
                ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                        .secure(true)
                        .maxAge(30 * 60)
                        .path("/") // 쿠키가 저장되는 페이지
                        .sameSite("Lax")
                        .httpOnly(true)
                        .build();
                response.addHeader("Set-Cookie", cookie.toString());
                return UserLoginResponseDto.builder().result(true).message("로그인에 성공했습니다.").build();
            } else {
                log.info("로그인 실패 - 비밀번호 오입력");
                response.setStatus(400);
                return UserLoginResponseDto.builder().result(false).message("로그인에 실패했습니다.").build();
            }
        }
        catch (NoSuchElementException e) {
            log.info("로그인 실패 - 존재하지 않는 ID");
            System.out.println("존재하지 않는 ID 입니다.");
            response.setStatus(404);
            return UserLoginResponseDto.builder().result(false).message("존재하지 않는 ID 입니다.").build();
        }
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveToken(request, "accessToken");
        if (accessToken != null) {
            ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                    .secure(true)
                    .maxAge(0)
                    .path("/") // 쿠키가 저장되는 페이지
                    .sameSite("Lax")
                    .httpOnly(true)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            long refreshTokenId = jwtTokenProvider.getRefreshTokenIdFromToken(accessToken);
            RefreshToken refreshToken = userDeatilsService.getRefreshTokenById(refreshTokenId);
            refreshToken.disableRefreshToken();
            refreshTokenRepository.save(refreshToken);
            return true;
        }
        return false;
    }
}
