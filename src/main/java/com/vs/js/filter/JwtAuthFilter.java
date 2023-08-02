package com.vs.js.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                System.out.println("여기");

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("bearer ")) {
            System.out.println("auth/login, auth/join은 인증모드 없이도 진행해도 됨");
            System.out.println("permitAll 해 놔서");
            System.out.println("header 안에 authorization이 안 들어왔으니 걍 인증 없이 해라");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("할루");
        UserDetails user = User.builder().username("qwer").password("").build();
        // 인증해라...
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,
                null, List.of(new SimpleGrantedAuthority("USER")));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 그 다음꺼 자동 진행
        filterChain.doFilter(request, response);

    }

}
