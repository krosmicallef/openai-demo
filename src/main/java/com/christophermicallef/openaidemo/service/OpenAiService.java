package com.christophermicallef.openaidemo.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    public static final String NO_MARKUP = " Respond using plain text only. Do not use Markdown, HTML, code blocks, or any formatting. Return only raw text.";
    private final WebClient webClient;
    private final String model;

    public OpenAiService(WebClient webClient,
                         @Value("${openai.model}") String model) {
        this.webClient = webClient;
        this.model = model;
    }

    public Mono<String> ask(String question) {
        question = sanitizeQuestion(question);
        question += NO_MARKUP;
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", question)
                )
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                });
    }

    private static String sanitizeQuestion(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String trimmed = input.trim();

        if (trimmed.endsWith("?")) {
            return trimmed + " ";
        } else {
            return trimmed + "? ";
        }
    }
}
