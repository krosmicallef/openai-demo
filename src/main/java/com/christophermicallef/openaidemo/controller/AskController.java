package com.christophermicallef.openaidemo.controller;

import com.christophermicallef.openaidemo.model.AskRequest;
import com.christophermicallef.openaidemo.model.AskResponse;
import com.christophermicallef.openaidemo.service.GeminiAiService;
import com.christophermicallef.openaidemo.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AskController {

    private final OpenAiService openAiService;
    private final GeminiAiService geminiAiService;

    public AskController(OpenAiService openAiService, GeminiAiService geminiAiService) {
        this.openAiService = openAiService;
        this.geminiAiService = geminiAiService;
    }

    @PostMapping("/ask")
    public Mono<AskResponse> ask(@RequestBody AskRequest request) {
        Mono<String> askResponse = "gemini".equals(request.getProvider()) ? geminiAiService.ask(request.getQuestion()) : openAiService.ask(request.getQuestion());
        return askResponse.map(AskResponse::new);
    }
}