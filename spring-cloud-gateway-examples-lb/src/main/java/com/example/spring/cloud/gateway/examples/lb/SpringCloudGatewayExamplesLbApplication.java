package com.example.spring.cloud.gateway.examples.lb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudGatewayExamplesLbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayExamplesLbApplication.class, args);
    }

    @Bean
    public RouteLocator custom2RouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route(r -> r.path("/use-lb/**")
                        .filters(f->f.rewritePath("/use-lb/(?<remaining>.*)", "/${remaining}")).uri("lb://EXAMPLE-SERVICE"))
                .build();
        //@formatter:on
    }
}
