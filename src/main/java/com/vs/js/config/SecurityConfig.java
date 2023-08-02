// session 대신 jwt 방식 //
package com.vs.js.config;

import java.sql.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.vs.js.JsApplication;
import com.vs.js.filter.JwtAuthFilter;
import com.vs.js.service.CustomOauth2UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity // spring 객체 설정하는 class
@Configuration // spring security 재정의하는 class
@RequiredArgsConstructor // @autowired안쓰고 private final 설정하는거 객체 주입함
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOauth2UserService oAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 보안 토큰 넘어와야 하는데 그거 안하겠다
                .csrf(csrf -> csrf.disable())
                // 크롬 브라우저 정책상 다른 요청을 허용하지 않는데 그거 안 하겠음
                .cors(cors -> cors.configurationSource(req -> {
                    var corsconfig = new CorsConfiguration();
                    corsconfig.setAllowedOrigins(List.of("*"));
                    corsconfig.setAllowedHeaders(List.of("*"));
                    corsconfig.setAllowedMethods(List.of("*"));
                    return corsconfig;
                }))
                .authorizeHttpRequests((ar) -> ar
                        // 회원가입 로긴 허용
                        .requestMatchers("/auth/**").permitAll()
                        // user로 요쳥도 허용
                        .requestMatchers("/user/**").permitAll()
                        // 다른것은 인증 필요->google login으로 갈꺼임
                        .anyRequest().authenticated())
                .oauth2Login((oauth2Login) -> oauth2Login
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(oAuth2UserService))
                        .successHandler((request, response, authenticate) -> {
                            response.setContentType("text/plain");
                            Claims claims = Jwts.claims();
                            claims.put("username", "hk");
                            String myToken = Jwts.builder()
                                    .setClaims(claims)
                                    .setIssuedAt(new Date(System.currentTimeMillis()))
                                    .setExpiration(new Date(System.currentTimeMillis()))
                                    .signWith(SignatureAlgorithm.HS512, JsApplication.KEY)
                                    .compact();
                            var writer = response.getWriter();
                            writer.println("success!");
                        }))
                // .formLogin(fo->fo.loginPage(null)) ->session 방식
                // session 방식 안쓰고 JWT 쓸 계획
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Username 체크하기 전에 jwtAuthFilter 객체 먼저 들어가야함
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
