package com.example.intercaller.controller;

import com.example.intercaller.model.CallmeRequest;
import com.example.intercaller.model.CallmeResponse;
import com.example.intercaller.model.Conversation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caller")
public class CallerController {

    private Integer id = 0;

    private final RestTemplate template;
    private final Resilience4JCircuitBreakerFactory factory;

    @PostMapping("/send/{message}")
    CallmeResponse send(@PathVariable String message) {
        var request = new CallmeRequest(++id, message);

        CallmeResponse callmeResponse = template
                .postForObject("http://inter-callme/callme/call",
                        request,
                        CallmeResponse.class);

        return callmeResponse;

    }

    @PostMapping("/random-send/{message}")
    CallmeResponse randomSend(@PathVariable String message) {
        var request = new CallmeRequest(++id, message);

        var circuit = factory.create("random-circuit");

        return circuit.run(() ->
                template.postForObject("http://inter-callme/callme/random-call",
                        request,
                        CallmeResponse.class));

    }

    @PostMapping("/slow-send/{message}")
    CallmeResponse slowSend(@PathVariable String message) {
        var request = new CallmeRequest(++id, message);

        return template
                .postForObject("http://inter-callme/callme/slow-call",
                        request,
                        CallmeResponse.class);

    }

    @GetMapping("/conversations")
    List<Conversation> findAllConversations() {
        Conversation[] conversations = template.
                getForObject("http://inter-callme/callme/conversations",
                        Conversation[].class);

        return Arrays.stream(conversations).collect(Collectors.toList());
    }

    @GetMapping("/conversations/{requestId}")
    Conversation findByRequestId(@PathVariable int requestId) {
        return template.getForObject("http://inter-callme/callme/conversations/{requestId}",
                Conversation.class,
                requestId);
    }

}
