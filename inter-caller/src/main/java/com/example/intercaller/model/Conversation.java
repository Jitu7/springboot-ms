package com.example.intercaller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    private LocalDateTime time;
    private CallmeRequest request;
    private CallmeResponse response;

}
