# gateway project
first add spring-cloud-starter-netflix-eureka-client dependency
``` xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
``` 

then add @EnableDiscoveryClient on Application class
```java
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudGatewayExamplesLbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayExamplesLbApplication.class, args);
    }

}
```

last add properties configuration in yml file
```yaml
# use eureka discovery, so add eureka discovery server config
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    enabled: true 
  instance:
    prefer-ip-address: true

# open gateway discovery locator and close non-flux loadbalancer
spring:
  application:
    name: spring-cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
    loadbalancer:
      ribbon:~~~~~~~~
        enabled: false
```

# auto path mapping
gateway will auto mapping /serviceId/** to serviceId's /**, e.g.
```
http://localhost:8080/EXAMPLE-SERVICE/hello
--->
http://localhost:890/hello
```

# self define path mapping
add a lb route like this:
```java
    @Bean
    public RouteLocator custom2RouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route(r -> r.path("/use-lb/**")
                        .filters(f->f.rewritePath("/use-lb/(?<remaining>.*)", "/${remaining}")).uri("lb://EXAMPLE-SERVICE"))
                .build();
        //@formatter:on
    }
```

# experience example
1. start spring-cloud-gateway-examples-eureka-server/SpringCloudGatewayExamplesEurekaServerApplication
2. start spring-cloud-gateway-examples-service/SpringCloudGatewayExamplesServiceApplication
3. start spring-cloud-gateway-examples-lb/SpringCloudGatewayExamplesLbApplication
4. open web browser and request `http://localhost:8080/EXAMPLE-SERVICE/hello` or `http://localhost:8080/use-lb/hello`