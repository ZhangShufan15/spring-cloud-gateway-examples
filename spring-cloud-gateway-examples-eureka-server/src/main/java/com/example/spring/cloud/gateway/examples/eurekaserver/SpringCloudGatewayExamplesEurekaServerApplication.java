package com.example.spring.cloud.gateway.examples.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringCloudGatewayExamplesEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayExamplesEurekaServerApplication.class, args);
    }

}
