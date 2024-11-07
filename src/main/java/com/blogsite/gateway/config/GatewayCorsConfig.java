package com.blogsite.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayCorsConfig implements WebFluxConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(GatewayCorsConfig.class);
    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
    private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String MAX_AGE = "3600";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            if (CorsUtils.isCorsRequest(exchange.getRequest())) {
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
                exchange.getResponse().getHeaders().add("Access-Control-Max-Age", MAX_AGE);
                if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
                    return Mono.empty();
                }
            }
            return chain.filter(exchange);
        };
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://34.56.110.16")  // React frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
