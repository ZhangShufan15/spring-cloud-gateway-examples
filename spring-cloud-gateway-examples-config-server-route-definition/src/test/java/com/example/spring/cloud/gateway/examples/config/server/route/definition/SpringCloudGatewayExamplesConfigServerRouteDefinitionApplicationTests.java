package com.example.spring.cloud.gateway.examples.config.server.route.definition;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.predicate.CookieRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(properties = {"route-definitions=[{\"id\": \"websocket_test\",\"uri\": \"ws://localhost:9000\",\"order\": 9000,\"predicates\":[{\"name\":\"Path\", \"args\":{\"_genkey_0\":\"/echo\"}}],\"filters\":[{\"name\":\"AddRequestHeader\", \"args\":{\"_genkey_0\":\"x-tt-token\",\"_genkey_1\":\"123456\"}}]}]"})
@SpringBootTest(properties = {"route-definitions=[{\"id\": \"websocket_test\",\"uri\": \"ws://localhost:9000\",\"order\": 9000,\"predicates\":[\"Path=/echo\"],\"filters\":[\"AddRequestHeader=x-tt-token, 123456\"]}]"})
class SpringCloudGatewayExamplesConfigServerRouteDefinitionApplicationTests {

    @Autowired
    ConfigServerRouteDefinitionLocator locator;
    @Autowired
    RouteDefinitionRouteLocator routeLocator;

    @Test
    void GetRouteDefinitions() {
        //locator.getRouteDefinitions().map(r -> {System.out.println("definition:" + r); return r;});

        StepVerifier.create(locator.getRouteDefinitions()).assertNext(route -> {
            List<PredicateDefinition> predicates = route.getPredicates();
            assertThat(predicates).hasSize(1);
            assertThat(predicates.toString()).contains("Path").contains("echo");
            List<FilterDefinition> filters = route.getFilters();
            assertThat(filters).hasSize(1);
            assertThat(filters.get(0).getArgs()).containsEntry("_genkey_0", "x-tt-token");
            assertThat(filters.get(0).getArgs()).containsEntry("_genkey_1", "123456");

        }).expectComplete().verify();

        StepVerifier.create(routeLocator.getRoutes()).assertNext(route -> {
            MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com/echo")
                    .build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            System.out.println(Mono.from(route.getPredicate().apply(exchange)).block());
            assertThat(Mono.from(route.getPredicate().apply(exchange)).block()).isTrue();

            DefaultGatewayFilterChain chain = new DefaultGatewayFilterChain(route.getFilters());
            chain.filter(exchange).block();
            assertThat(exchange.getRequest().getHeaders().get("x-tt-token").contains("123456")).isTrue();
            System.out.println(exchange.getRequest().getHeaders().toString());
        }).expectComplete().verify();
    }

    static class DefaultGatewayFilterChain implements GatewayFilterChain {
        private final int index;
        private final List<GatewayFilter> filters;

        DefaultGatewayFilterChain(List<GatewayFilter> filters) {
            this.filters = filters;
            this.index = 0;
        }

        private DefaultGatewayFilterChain(DefaultGatewayFilterChain parent, int index) {
            this.filters = parent.getFilters();
            this.index = index;
        }

        public List<GatewayFilter> getFilters() {
            return this.filters;
        }

        public Mono<Void> filter(ServerWebExchange exchange) {
            return Mono.defer(() -> {
                if (this.index < this.filters.size()) {
                    GatewayFilter filter = (GatewayFilter)this.filters.get(this.index);
                    DefaultGatewayFilterChain chain = new DefaultGatewayFilterChain(this, this.index + 1);
                    return filter.filter(exchange, chain);
                } else {
                    return Mono.empty();
                }
            });
        }
    }

    @TestConfiguration
    static class TestConfiruration {
        @Bean
        public ConfigServerRouteDefinitionLocator definitionLocator(){
            return new ConfigServerRouteDefinitionLocator();
        }
    }

}
