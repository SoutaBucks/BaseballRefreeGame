package com.baseballrefree.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Random;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  // 투구/타구/선수 생성에 쓰는 난수원. 테스트에서는 시드 고정 Random으로 직접 주입니다.
  @Bean
  public Random random() { return new Random(); }

  // 프론트엔드 개발 서버에서 /api/** 호출을 허용한다
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "Post");
  }
}
