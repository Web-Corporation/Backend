package com.sketch.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        if (path.startsWith("/users/login") || path.startsWith("/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더가 없거나 잘못된 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 반환
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        // JWT 유효성 검증 실패 시 401 반환
        if (!jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 반환
            return;
        }

        // JWT가 유효하다면 SecurityContext 설정
        String username = jwtTokenProvider.extractUsername(token);
        UserDetails userDetails = User.withUsername(username)
                .password("") // 비밀번호 필요 없음
                .authorities(new ArrayList<>()) // 권한 정보 없음
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 필터 체인의 다음 단계로 요청 전달
        filterChain.doFilter(request, response);
    }

}