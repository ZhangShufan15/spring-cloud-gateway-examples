package com.example.spring.cloud.gateway.examples.config.server.route.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * a route definition locator, that locate route definition from remote config server or local properties file.
 * route definition is json value, not yaml value, e.g.
 * "route-definitions=[{\"id\": \"websocket_test\",\"uri\": \"ws://localhost:9000\",\"order\": 9000,\"predicates\":[\"Path=/echo\"],\"filters\":[\"AddRequestHeader=x-tt-token, 123456\"]}]"
 * or
 * "route-definitions=[{\"id\": \"websocket_test\",\"uri\": \"ws://localhost:9000\",\"order\": 9000,\"predicates\":[{\"name\":\"Path\", \"args\":{\"_genkey_0\":\"/echo\"}}],\"filters\":[{\"name\":\"AddRequestHeader\", \"args\":{\"_genkey_0\":\"x-tt-token\",\"_genkey_1\":\"123456\"}}]}]"
 *
 * compare to spring cloud gateway's default InMemoryRouteDefinitionRepository, route definitions could be loaded when program's startup.
 *
 * to do this, there is another way. define a bean and load remote route definitions to InMemoryRouteDefinitionRepository when program's startup.
 * otherwise, you should implement ApplicationListener<ApplicationEvent>. when refresh event emited, remote route definitions should be reloaded
 * into InMemoryRouteDefinitionRepository.
 */
public class ConfigServerRouteDefinitionLocator implements RouteDefinitionLocator {
    @Value("${route-definitions}")
    private String routeDefinitions;//a json string, contains route definition lists

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        ObjectMapper mapper = new ObjectMapper();
        List<RouteDefinition> rdList = null;
        try {
            System.out.println(routeDefinitions);
            rdList = mapper.readValue(routeDefinitions, new TypeReference<List<RouteDefinition>>() { });
            return Flux.fromArray(rdList.toArray(new RouteDefinition[]{}));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Flux.empty();
    }
}
