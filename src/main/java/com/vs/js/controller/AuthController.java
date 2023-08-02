package com.vs.js.controller;

import java.sql.Date;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vs.js.JsApplication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
// 인증이 없어도 되는 요청
@RestController // html 파일 찾지 않고 문자열 보내기
@RequestMapping("auth") // auth 경로로 시작하면 여기 실행
public class AuthController {
    @PostMapping("join")
    public String join() {
        return "join success";
    }

    @PostMapping("login")
    public String login() {
        Claims claims = Jwts.claims();
        claims.put("username", "hk");
        String myToken = Jwts.builder()
                // username 도 추가해서 암호화 함
                .setClaims(claims)
                // 현재 시간으로 발생
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 5분뒤 사용 불가
                .setExpiration(new Date(System.currentTimeMillis()))
                // KEY값으로 암호화함
                .signWith(SignatureAlgorithm.HS512, JsApplication.KEY)
                .compact();
        return myToken;
    }
}
