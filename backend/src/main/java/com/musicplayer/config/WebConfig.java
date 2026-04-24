package com.musicplayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/covers/**")
                .addResourceLocations("file:./covers/");
        registry.addResourceHandler("/wallpapers/**")
                .addResourceLocations("file:./wallpapers/");
    }
}