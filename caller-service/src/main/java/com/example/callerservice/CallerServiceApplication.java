package com.example.callerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class CallerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallerServiceApplication.class, args);
    }

}
