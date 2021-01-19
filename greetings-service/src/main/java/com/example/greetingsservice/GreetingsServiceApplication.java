package com.example.greetingsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SpringBootApplication
public class GreetingsServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreetingsServiceApplication.class, args);
  }

}

@RestController
class GreetingsRestController {

  @GetMapping("/greetings/{name}")
  Map<String, String> greeting(@PathVariable String name,
                               @RequestHeader Map<String, String> map,
                               @RequestHeader("x-forwarded-host") Optional<String> host,
                               @RequestHeader("x-forwarded-port") Optional<Integer> port) {

    System.out.println(map);

    host.ifPresent(h -> System.out.println("host = " + h));
    port.ifPresent(p -> System.out.println("port = " + p));

    return Collections.singletonMap("greeting", "Hello " + name + "😀");
  }

}
