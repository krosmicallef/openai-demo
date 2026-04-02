package com.christophermicallef.openaidemo.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
//
//    @Bean
//    public HttpClient httpClient() {
//        return HttpClient.create()
//                .resolver(DefaultAddressResolverGroup.INSTANCE);
//    }

    @Bean
    public WebClient webClient(@Value("${openai.api-key}") String apiKey) {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}