package io.github.parkjeongwoong.config;

import io.github.parkjeongwoong.adapter.filter.JwtAuthenticationFilter;
import io.github.parkjeongwoong.adapter.handler.CustomAccessDeniedHandler;
import io.github.parkjeongwoong.adapter.handler.CustomAuthenticationEntryPoint;
import io.github.parkjeongwoong.application.user.service.JwtTokenProvider;
import io.github.parkjeongwoong.application.user.service.UserDeatilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDeatilsService userDeatilsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // h2 콘솔 사용
            .csrf().disable()
            .headers().frameOptions().disable().and()
            // 세션 사용 X
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            // URL 권한 관리
            .authorizeRequests()
            .antMatchers("/data-api/download/**", "/blog-api/articleList", "/user-api/authtest").authenticated() // 인증 필요
            .antMatchers("/h2-console", "/blog/upload", "/blog-api/article/upload/**", "/user-api/admintest",
                    "/recommend/make-similarity-index/**", "/search/make-inverted-index").hasRole("ADMIN") // 인증 & ADMIN 권한 필요
            .anyRequest().permitAll().and() // 다른 모든 Request -> 인증 불필요
            .exceptionHandling() // 인증/인가 실패에 따른 리다이렉트
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()).and()
            .formLogin().loginPage("/blog/login").and()
            .logout().logoutSuccessUrl("/").and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDeatilsService),
                             UsernamePasswordAuthenticationFilter.class); // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 추가


        // 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스를 생성
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
