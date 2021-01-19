package com.example.intercallme.controller;

import com.example.intercallme.model.CallmeRequest;
import com.example.intercallme.model.CallmeResponse;
import com.example.intercallme.model.Conversation;
import com.example.intercallme.repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/callme")
public class CallmeController {

    @Value("${app.delay}")
    private Long delay;

    private final ConversationRepository repository;

    @PostMapping("/call")
    CallmeResponse call(@RequestBody CallmeRequest request) {
        var response =
                new CallmeResponse(request.getId(), request.getMessage());

        repository.add(
                new Conversation(LocalDateTime.now(), request, response));

        return response;
    }

    @PostMapping("/random-call")
    CallmeResponse randomCall(@RequestBody CallmeRequest request) throws InterruptedException {

        long r = new Random(delay).nextLong();

        log.info("Generated delay: {}", r);

        if (delay != 0L)
            Thread.sleep(r);

        log.info("Req: message->{}, delay->{}", request.getMessage(), r);

        var response =
                new CallmeResponse(request.getId(), request.getMessage());

        repository.add(
                new Conversation(LocalDateTime.now(), request, response));

        return response;
    }

    @SneakyThrows
    @PostMapping("/slow-call")
    CallmeResponse slowCall(@RequestBody CallmeRequest request) {
        Thread.sleep(1000);

        var response =
                new CallmeResponse(request.getId(), request.getMessage());

        repository.add(
                new Conversation(LocalDateTime.now(), request, response));

        return response;
    }

    @GetMapping("/conversations")
    List<Conversation> findAllConversations() {
        return repository.findAll();
    }

    @GetMapping("/conversations/{requestId}")
    Conversation findConversationByRequestId(@PathVariable int requestId) {
        return repository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Not Found"));
    }

}
