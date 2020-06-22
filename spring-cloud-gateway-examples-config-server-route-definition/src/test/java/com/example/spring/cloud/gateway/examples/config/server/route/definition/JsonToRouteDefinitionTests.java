package com.example.spring.cloud.gateway.examples.config.server.route.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

@SpringBootTest
public class JsonToRouteDefinitionTests {

    @Test
    void jsonToDefinition() throws JsonProcessingException {
        String js = "[{\"id\": \"websocket_test\","
                + "\"uri\": \"ws://localhost:9000\","
                + "\"order\": 9000,"
                + "\"predicates\":[" +
                "{\"name\":\"Path\", \"args\":{\"1\":\"/echo\"}}]}]";
        ObjectMapper mapper = new ObjectMapper();
        List<RouteDefinition> rdList = mapper.readValue(js, new TypeReference<List<RouteDefinition>>() { });
        System.out.println(rdList);
    }
}
