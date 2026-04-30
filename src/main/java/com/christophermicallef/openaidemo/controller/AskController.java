package com.christophermicallef.openaidemo.controller;

import com.christophermicallef.openaidemo.model.AskRequest;
import com.christophermicallef.openaidemo.model.AskResponse;
import com.christophermicallef.openaidemo.service.GeminiAiService;
import com.christophermicallef.openaidemo.service.OpenAiSDKService;
import com.christophermicallef.openaidemo.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AskController {

    private final OpenAiService openAiService;
    private final GeminiAiService geminiAiService;
    private final OpenAiSDKService openAiSDKService;

    public AskController(OpenAiService openAiService, GeminiAiService geminiAiService, OpenAiSDKService openAiSDKService) {
        this.openAiService = openAiService;
        this.geminiAiService = geminiAiService;
        this.openAiSDKService = openAiSDKService;
    }

    @PostMapping("/ask")
    public Mono<AskResponse> ask(@RequestBody AskRequest request) {
        Mono<String> askResponse;
        switch (request.getProvider()) {
            case "gemini" -> askResponse = geminiAiService.ask(request.getQuestion());
            case "openai" -> askResponse = openAiService.ask(request.getQuestion());
            case "openaisdk" -> askResponse = openAiSDKService.ask(request.getQuestion());
            default -> throw new IllegalArgumentException("Unsupported provider: " + request.getProvider());
        }
        return askResponse.map(AskResponse::new);
    }
}