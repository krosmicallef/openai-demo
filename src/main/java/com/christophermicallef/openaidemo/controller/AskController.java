package com.christophermicallef.openaidemo.controller;

import com.christophermicallef.openaidemo.model.AskRequest;
import com.christophermicallef.openaidemo.model.AskResponse;
import com.christophermicallef.openaidemo.service.OpenAiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AskController {

    private final OpenAiService openAiService;

    public AskController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/ask")
    public Mono<AskResponse> ask(@RequestBody AskRequest request) {
        return openAiService.ask(request.getQuestion())
                .map(AskResponse::new);
    }
}