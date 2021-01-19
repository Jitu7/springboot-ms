package com.example.callerservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caller")
public class CallerController {

    @Value("${property1}")
    private String property1;

    @Value("${property2}")
    private String property2;

    @GetMapping("/property1")
    public String findProperty1() {
        return property1;
    }

    @GetMapping("/property2")
    public String findProperty2() {
        return property2;
    }

}
