package com.example.apigateway.controller;

import com.example.apigateway.model.GatewayResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallBackController {

    private int id = 0;

    @PostMapping("/test")
    Mono<GatewayResponse> fallback() {
        return Mono.just(new GatewayResponse(++id, "I'm fallback"));
    }

}
