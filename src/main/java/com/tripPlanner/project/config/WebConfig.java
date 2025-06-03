package com.tripPlanner.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        System.out.println("file:///"+uploadDir);
        registry
                .addResourceHandler("/uploads/**")
                //.addResourceLocations("file:///" + uploadDir);
                .addResourceLocations("file:///" + uploadDir.replace("\\", "/")); // 슬래시 변환 필수
    }

}
