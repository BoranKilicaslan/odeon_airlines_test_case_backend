package com.odeon.odeon_airlines.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tüm yollar için geçerli
                .allowedOrigins("http://localhost:3000") // İzin verilen origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen HTTP metodları
                .allowedHeaders("*") // İzin verilen başlıklar
                .allowCredentials(true); // Kimlik bilgilerini kabul et
    }
}
