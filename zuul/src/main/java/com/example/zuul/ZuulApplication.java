package com.example.zuul;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableCircuitBreaker
@EnableFeignClients
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {

//  @Bean
//  @LoadBalanced
//  RestTemplate restTemplate() {
//    return new RestTemplateBuilder().build();
//  }

  public static void main(String[] args) {
    SpringApplication.run(ZuulApplication.class, args);
  }

}

@FeignClient("greetings-service")
interface GreetingsClient {

  @GetMapping("/greetings/{name}")
  Greeting greeting(@PathVariable String name);

}

@RestController
@RequiredArgsConstructor
class GreetingsApiGateWayRestController {

//  private final RestTemplate restTemplate;

  private final GreetingsClient greetingsClient;

  public String fallback(String name) {
    return "Failed 🙃";
  }

  @HystrixCommand(fallbackMethod = "fallback")
  @GetMapping("/hi/{name}")
  String greet(@PathVariable String name) {
    return this.greetingsClient
     .greeting(name)
     .getGreeting();
  }

  /*@GetMapping("/hi/{name}")
  String greet(@PathVariable String name) {
    final ResponseEntity<Greeting> responseEntity = this.restTemplate.exchange(
     "http://greetings-service/greetings/{name}",
     HttpMethod.GET, null, Greeting.class, name);

    return responseEntity.getBody().getGreeting();
  }*/

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Greeting {
  private String greeting;
}

//@Component
class RateLimitingZuulFilter extends ZuulFilter {

  private final RateLimiter rateLimiter = RateLimiter.create(1.0 / 30.0); //  1 req every 30th of sec

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 100;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {

    try {
      final var currentContext = RequestContext.getCurrentContext();

      final HttpServletResponse response = currentContext.getResponse();

      if (!rateLimiter.tryAcquire()) {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().append(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
        currentContext.setSendZuulResponse(false);

      }

    } catch (IOException e) {
      ReflectionUtils.rethrowRuntimeException(e);
    }

    return null;
  }

}
