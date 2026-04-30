package com.christophermicallef.openaidemo.service;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OpenAiSDKService implements IAiService {

    private final OpenAIClient openAIClient;
    private final String model;

    public OpenAiSDKService(OpenAIClient openAIClient, @Value("${openai.model}") String model) {
        this.openAIClient = openAIClient;
        this.model = model;
    }

    @Override
    public Mono<String> ask(String question) {
        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model(model)
                        .addUserMessage(question)
                        .build();
        ChatCompletion completion = openAIClient.chat().completions().create(params);
        String content = completion.choices().getFirst().message().content().orElseThrow();
        return Mono.just(content);
    }
}
