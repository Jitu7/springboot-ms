package com.example.intercaller.controller;

import com.example.intercaller.client.CallmeClient;
import com.example.intercaller.model.CallmeRequest;
import com.example.intercaller.model.CallmeResponse;
import com.example.intercaller.model.Conversation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caller-with-feign")
public class CallerControllerWithFeign {

    private Integer id = 0;

    private final CallmeClient client;

    @PostMapping("/send/{message}")
    CallmeResponse send(@PathVariable String message) {
        var request = new CallmeRequest(++id, message);

        CallmeResponse callmeResponse = client.call(request);

        return callmeResponse;

    }

    @PostMapping("/slow-send/{message}")
    CallmeResponse slowSend(@PathVariable String message) {
        var request = new CallmeRequest(++id, message);

        CallmeResponse callmeResponse = client.slowCall(request);

        return callmeResponse;
    }

    @GetMapping("/conversations")
    List<Conversation> findAllConversations() {

        List<Conversation> conversations = client.findAllConversations();
        return conversations;

    }

    @GetMapping("/conversations/{requestId}")
    Conversation findByRequestId(@PathVariable int requestId) {

        Conversation conversation = client.findConversationByRequestId(requestId);
        return conversation;

    }

}
