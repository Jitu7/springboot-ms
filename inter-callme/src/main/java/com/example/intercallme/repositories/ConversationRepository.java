package com.example.intercallme.repositories;

import com.example.intercallme.model.Conversation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ConversationRepository {

    private final List<Conversation> conversations = new ArrayList<>();

    public Optional<Conversation> findByRequestId(int requestId) {
        return conversations.stream()
                .filter(conversation -> conversation.getRequest().getId().equals(requestId))
                .findFirst();
    }

    public List<Conversation> findAll() {
        return conversations;
    }

    public void add(Conversation conversation) {
        conversations.add(conversation);
    }
}
