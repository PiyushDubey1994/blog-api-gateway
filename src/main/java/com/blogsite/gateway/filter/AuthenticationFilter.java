package com.blogsite.gateway.filter;

import com.blogsite.gateway.tokenutill.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(RouteValidator.class);

    @Autowired
    private RouteValidator validator;

    //    @Autowired
//    private RestTemplate template;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            String authHeader = null;
            List<String> roles = new ArrayList<>();
            if (validator.isSecured.test(exchange.getRequest())) {

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.error("missing authorization header");
                }

                authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {

//                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);
//                    roles = jwtUtil.extractRoles(authHeader);
//                    // Check if the user has access to the route based on their roles
//                    if (isAdminRoute(exchange) && !hasAnyRole(roles, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
//                        logger.info("User does not have admin privileges");
//                    }
//                    if (isUserRoute(exchange) && !hasAnyRole(roles, "ROLE_USER", "ROLE_ADMIN")) {
//                        logger.info("User does not have user privileges");
//                    }
                    return chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader)
                                    .build())
                            .build());


                } catch (Exception e) {
                    logger.error("invalid access...!");
                    logger.error("un authorized access to application");
                }
            }
            if (authHeader != null) {
                return chain.filter(exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader)
                                .build())
                        .build());
            } else {
                return chain.filter(exchange);
            }

        });

    }

    private boolean hasAnyRole(List<String> userRoles, String... requiredRoles) {
        for (String requiredRole : requiredRoles) {
            if (userRoles.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminRoute(ServerWebExchange exchange1) {
        String path = exchange1.getRequest().getURI().getPath();
        return path.contains("/user");  // Example route for admins
    }

    private boolean isUserRoute(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return path.contains("/user");
    }

    public static class Config {

    }
}