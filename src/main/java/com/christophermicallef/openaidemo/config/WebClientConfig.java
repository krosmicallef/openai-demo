package com.christophermicallef.openaidemo.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientOpenAi(@Value("${openai.api-key}") String apiKey, @Value("${openai.url}") String apiUrl) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient webClientGemini(@Value("${gemini.api-key}") String apiKey, @Value("${gemini.url}") String apiUrl) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public OpenAIClient openAIClient(@Value("${openai.api-key}") String apiKey) {
        return OpenAIOkHttpClient.builder().apiKey(apiKey)
                .build();
    }
}