package com.example.intercaller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableFeignClients
@SpringBootApplication
public class InterCallerApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
//                .setReadTimeout(Duration.ofMillis(100))
//                .setReadTimeout(Duration.ofMillis(100))
                .build();
    }

    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return resilience4JCircuitBreakerFactory -> resilience4JCircuitBreakerFactory
                .configureDefault(id ->
                        new Resilience4JConfigBuilder(id)
                                .timeLimiterConfig(TimeLimiterConfig
                                        .custom()
                                        .timeoutDuration(Duration.ofMillis(500))
                                        .build())
                                .circuitBreakerConfig(CircuitBreakerConfig
                                        .custom()
                                        .slidingWindowSize(10)
                                        .failureRateThreshold(66.6F)
                                        .slowCallRateThreshold(60)
                                        .build())
                                .build());
    }

    public static void main(String[] args) {
        SpringApplication.run(InterCallerApplication.class, args);
    }

}
