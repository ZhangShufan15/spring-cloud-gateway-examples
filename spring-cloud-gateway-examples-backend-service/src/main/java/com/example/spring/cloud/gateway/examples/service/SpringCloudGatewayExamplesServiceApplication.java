package com.example.spring.cloud.gateway.examples.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SpringCloudGatewayExamplesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayExamplesServiceApplication.class, args);
    }

    @RequestMapping("/hello")
    public String home() {
        return "Hello World";
    }
}
