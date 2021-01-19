package com.example.intercaller.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FailingRestController {


}

@Service
class FailingService {

    String greet(String name) {
        return "Hello " + name + "!";

    }
}