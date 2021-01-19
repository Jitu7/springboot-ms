package com.example.intercaller.client;

import com.example.intercaller.model.CallmeRequest;
import com.example.intercaller.model.CallmeResponse;
import com.example.intercaller.model.Conversation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inter-callme", path = "/callme")
public interface CallmeClient {

    @PostMapping("/call")
    CallmeResponse call(@RequestBody CallmeRequest request);

    @GetMapping("/conversations")
    List<Conversation> findAllConversations();

    @GetMapping("/conversations/{requestId}")
    Conversation findConversationByRequestId(@PathVariable int requestId);

    @PostMapping("/slow-call")
    CallmeResponse slowCall(@RequestBody CallmeRequest request);

}