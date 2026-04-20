package com.christophermicallef.openaidemo.service;

import reactor.core.publisher.Mono;

public interface IAiService  {

    Mono<String> ask(String question);
}
